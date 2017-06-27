package com.qualcomm.robotcore.util;

/*
 * Copyright (c) 2016 Craig MacFarlane
 *   Based upon work by David Sargent and Bob Atkinson
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Craig MacFarlane nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.content.Context;
import android.util.Log;

import org.firstinspires.ftc.robotcore.internal.AppUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Finds all of the classes in the APK and provides an extensible mechanism
 * for iterating and selecting classes for particular needs.  Classes that want
 * to select particular classes should implement ClassFilter and register
 * themselves here.  See ClassManagerFactory.
 *
 * This is predicated on the notion that the set of classes in any given APK is
 * constant.  So a class may implement ClassFilter, populate a static list of
 * classes it is interested in, and then use that static list in any instance of
 * the class knowing that the list is not dynamic over any given install of the APK.
 * See AnnotatedOpModeRegistrar.
 */
public class ClassManager {

    private static final String LOGGING_TAG = "ClassManager";

    private List<Class> allClasses;
    private List<String> packagesAndClassesToIgnore;
    private List<ClassFilter> filters;
    private Context context;
    private DexFile dexFile;

    public ClassManager() throws IOException
    {
        // We ignore certain packages to make us more robust
        this.packagesAndClassesToIgnore = new LinkedList<String>();
        this.packagesAndClassesToIgnore.add("com.google");
        this.packagesAndClassesToIgnore.add("io.netty");

        this.context = AppUtil.getInstance().getApplication();
        this.dexFile = new DexFile(this.context.getPackageCodePath());

        this.filters = new LinkedList<ClassFilter>();
    }

    /**
     * You want to know what classes are in the APK?  Call me.
     *
     * @param filter a class that implements ClassFilter.
     */
    public void registerFilter(ClassFilter filter)
    {
        filters.add(filter);
    }

    /**
     * Find all the classes in the context in which we should consider looking, which
     * (currently?) is the entire .APK in which we are found.
     */
    private List<Class> findAllClasses()
    {
        List<Class> result = new LinkedList<Class>();

        /*
         * Classes can be found in either the base apk dex file, or any number of
         * instant run dex files.  Gather them all...
         */
        LinkedList<String> classNames = new LinkedList<String>(Collections.list(dexFile.entries()));
        List<String> instantRunClassNames = InstantRunDexHelper.getAllClasses(context);
        classNames.addAll(instantRunClassNames);

        for (String className : classNames)
        {
            // Ignore classes that are in some packages that we know aren't worth considering
            boolean shouldIgnore = false;
            for (String packageName : packagesAndClassesToIgnore)
            {
                if (Util.isPrefixOf(packageName, className))
                {
                    shouldIgnore = true;
                    break;
                }
            }
            if (shouldIgnore)
                continue;

            // Get the Class from the className
            Class clazz;
            try {
                clazz = Class.forName(className, false, context.getClassLoader());
            }
            catch (NoClassDefFoundError|ClassNotFoundException ex)
            {
                // We can't find that class
                if (logClassNotFound(className)) Log.w(LOGGING_TAG, className + " " + ex.toString(), ex);
                if (className.contains("$"))
                {
                    // Prevent loading similar inner classes, a performance optimization
                    className = className.substring(0, className.indexOf("$") /*- 1*/);
                }

                packagesAndClassesToIgnore.add(className);
                continue;
            }

            // Remember that class
            result.add(clazz);
        }

        return result;
    }

    protected boolean logClassNotFound(String className)
    {
        return !className.startsWith("com.vuforia.");
    }

    /**
     * Iterate over all the classes in the APK and call registered filters.
     */
    public void processAllClasses()
    {
        this.allClasses = findAllClasses();

        for (Class clazz : this.allClasses) {
            for (ClassFilter f : filters) {
                f.filter(clazz);
            }
        }
    }


}
