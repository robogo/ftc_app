/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.robotcore.internal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.robotcore.R;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.PeerApp;
import com.qualcomm.robotcore.util.ClassUtil;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.network.NetworkConnectionHandler;
import org.firstinspires.ftc.robotcore.internal.network.RobotCoreCommandList;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * {@link AppUtil} contains a few utilities related to application and activity management.
 */
@SuppressWarnings("WeakerAccess")
public class AppUtil
    {
    //----------------------------------------------------------------------------------------------
    // Static State
    //----------------------------------------------------------------------------------------------

    public static final String TAG= "AppUtil";

    // FIRST_FOLDER is the root of the tree we use in non-volatile storage (for everything except logs)
    public static final File FIRST_FOLDER = new File(Environment.getExternalStorageDirectory() + "/FIRST/");

    // ROBOT_SETTINGS is a folder in which it's convenient to store team-generated settings
    // associated with their robot
    public static final File ROBOT_SETTINGS = new File(FIRST_FOLDER, "/settings/");

    /** ROBOT_DATA_DIR is a convenient place in which to put persistent data created by your opmode */
    public static final File ROBOT_DATA_DIR = new File(FIRST_FOLDER, "/data/");

    /** UPDATES_DIR is a folder used to manage updates to firmware, installed APKs, and other components */
    public static final File UPDATES_DIR = new File(FIRST_FOLDER, "/updates/");

    private static AppUtil theInstance;
    public static AppUtil getInstance()
        {
        return theInstance;
        }

    public static Context getDefContext()
        {
        return getInstance().getApplication();
        }

    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    private LifeCycleMonitor    lifeCycleMonitor;
    private Activity            rootActivity;
    private Activity            currentActivity;
    private Method              methodCurrentApplication;
    private PeerApp             thisApp;
    private ProgressDialog      currentProgressDialog;
    private AlertDialogContext  currentAlertDialog;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    /**
     * This method is internal to the FTC SDK and should not be used.
     */
    public AppUtil(Application application)
        {
        lifeCycleMonitor = new LifeCycleMonitor();
        rootActivity     = null;
        currentActivity  = null;
        methodCurrentApplication = null;
        currentProgressDialog = null;
        currentAlertDialog = null;

        application.registerActivityLifecycleCallbacks(lifeCycleMonitor);

        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            methodCurrentApplication = activityThreadClass.getMethod("currentApplication");
            }
        catch (ClassNotFoundException|NoSuchMethodException e)
            {
            // should never happen: the methods we use are all good to go
            }

        RobotLog.vv(TAG, "initializing: getExternalStorageDirectory()=%s", Environment.getExternalStorageDirectory());
        Assert.assertNull(theInstance);
        theInstance = this;
        }

    //----------------------------------------------------------------------------------------------
    // File and Directory Management
    //----------------------------------------------------------------------------------------------

    /**
     * Given a root File and a child underneath same returns the path from the former to the latter
     */
    public File getRelativePath(File root, File child)
        {
        File result = new File("");
        while (!root.equals(child))
            {
            File parent = child.getParentFile();
            result = new File(new File(child.getName()), result.getPath());
            if (parent == null) break;
            child = parent;
            }
        return result;
        }

    /**
     * Make sure all the components of the path exist, notifying MTP if necessary for any creations
     */
    public void ensureDirs(File dirs)
        {
        if (!dirs.exists())
            {
            File parent = dirs.getParentFile();
            if (parent != null)
                {
                ensureDirs(parent);
                }

            if (dirs.mkdir())
                {
                // successfully newly created. Notify MTP
                noteFileInMediaTransferProtocol(dirs);
                }
            else
                {
                // already existed, or error; latter ignored
                }
            }
        }

    /**
     * A public file has been updated or created. Inform the MediaScanner of this
     * fact so that it will show up in Media Transfer Protocol UIs on connected
     * desktop computers. This is necessary due to a very-long-standing bug in Android.
     *
     * @param file the file or directory that is to be noted
     * @see <a href="https://code.google.com/p/android/issues/detail?id=195362">Android bug</a>
     */
    public void noteFileInMediaTransferProtocol(File file)
        {
        List<String> paths = new ArrayList<String>();
        paths.add(file.getAbsolutePath());
        noteFileInMediaTransferProtocol(paths);
        }

    public void noteFileInMediaTransferProtocol(List<String> paths)
        {
        // We hear odd things about running this on directories, so for now at least we don't.
        ArrayList<String> scanList = new ArrayList<String>();
        for (String path : paths)
            {
            File file = new File(path);
            if (!file.isDirectory())
                {
                // RobotLog.vv(RobotLog.TAG, "mtp: scanning: %s", path);
                scanList.add(path);
                }
            else
                {
                // RobotLog.vv(RobotLog.TAG, "mtp: ignoring: %s", path);
                }
            }

        String[] scanArray = new String[scanList.size()];
        scanArray = scanList.toArray(scanArray);
        MediaScannerConnection.scanFile(
                getDefContext(),
                scanArray,
                null,
                null);
        }

    public File getSettingsFile(String filename)
        {
        File file = new File(filename);
        if (!file.isAbsolute())
            {
            ensureDirs(ROBOT_SETTINGS);
            file = new File(ROBOT_SETTINGS, filename);
            }
        return file;
        }

    //----------------------------------------------------------------------------------------------
    // Life Cycle
    //----------------------------------------------------------------------------------------------

    /**
     * Restarts the current application
     * @param exitCode the exit code to return from the current app run
     */
    public void restartApp(int exitCode)
        {
        // See http://stackoverflow.com/questions/2681499/android-how-to-auto-restart-application-after-its-been-force-closed
        RobotLog.vv(TAG, "restarting app");

        @SuppressWarnings("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplication().getBaseContext(),
                0,
                new Intent(rootActivity.getIntent()),
                rootActivity.getIntent().getFlags());

        int msRestartDelay = 1500;
        AlarmManager alarmManager = (AlarmManager) rootActivity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + msRestartDelay, pendingIntent);
        System.exit(exitCode);
        }

    public void finishRootActivityAndExitApp()
        {
        synchronousRunOnUiThread(new Runnable()
            {
            @Override public void run()
                {
                RobotLog.vv(TAG, "finishRootActivityAndExitApp()");
                if (Build.VERSION.SDK_INT >= 21)
                    {
                    rootActivity.finishAndRemoveTask();
                    }
                else
                    {
                    rootActivity.finish();
                    }
                exitApplication();
                }
            });
        }

    public void exitApplication(int resultCode)
        {
        RobotLog.vv(TAG, "exitApplication(%d)", resultCode);
        System.exit(resultCode);
        }

    public void exitApplication()
        {
        exitApplication(0);
        }

    //----------------------------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------------------------

    /**
     * Returns the contextually running {@link Application}
     * @return the contextually running {@link Application}
     */
    public @NonNull Application getApplication()
        {
        return (Application) ClassUtil.invoke(null, methodCurrentApplication);
        }

    public void setThisApp(@NonNull PeerApp thisApp)
        {
        this.thisApp = thisApp;
        }

    // @NonNull because RC and DS apps set it before anyone has a change to get it
    public @NonNull PeerApp getThisApp()
        {
        Assert.assertNotNull(this.thisApp);
        return this.thisApp;
        }

    //----------------------------------------------------------------------------------------------
    // UI interaction
    //----------------------------------------------------------------------------------------------

    /**
     * This works around a deliberate bug Google introduced to prevent options menus from working
     * on large screens. It is a hack, in the classic sense of the word. But it works. Onward...
     *
     * @param activity the guy whose options menu is to be opened
     * @see <a href="http://stackoverflow.com/questions/9996333/openoptionsmenu-function-not-working-in-ics/17903128#17903128">discussion</a>
     */
    public void openOptionsMenuFor(Activity activity)
        {
        Configuration config = activity.getResources().getConfiguration();
        if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > Configuration.SCREENLAYOUT_SIZE_LARGE)
            {
            int originalScreenLayout = config.screenLayout;
            config.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;
            try {
                activity.openOptionsMenu();
                }
            finally
                {
                config.screenLayout = originalScreenLayout;
                }
            }
        else
            {
            activity.openOptionsMenu();
            }
        }

    /**
     * A drop-in replacement for {@link Activity#runOnUiThread(Runnable) runonUiThread()} which doesn't
     * return until the UI action is complete.
     * @param action the action to perform on the UI thread
     */
    public void synchronousRunOnUiThread(final Runnable action)
        {
        synchronousRunOnUiThread(getActivity(), action);
        }

    public void synchronousRunOnUiThread(Activity activity, final Runnable action)
        {
        try {
            final CountDownLatch uiDone = new CountDownLatch(1);
            activity.runOnUiThread(new Runnable()
                {
                @Override public void run()
                    {
                    action.run();
                    uiDone.countDown();
                    }
                });
            uiDone.await();
            }
        catch (InterruptedException e)
            {
            Thread.currentThread().interrupt();
            }
        }

    /**
     * A simple helper so that callers have syntactically similar forms for both synchronous and non.
     */
    public void runOnUiThread(final Runnable action)
        {
        runOnUiThread(getActivity(), action);
        }

    public void runOnUiThread(Activity activity, final Runnable action)
        {
        activity.runOnUiThread(action);
        }

    public void showWaitCursor(@NonNull final String message, @NonNull final Runnable runnable)
        {
        showWaitCursor(message, runnable, null);
        }

    public void showWaitCursor(@NonNull final String message, @NonNull final Runnable backgroundWorker, @Nullable final Runnable runPostOnUIThread)
        {
        this.runOnUiThread(new Runnable()
            {
            @Override public void run()
                {
                new AsyncTask<Object,Void,Void>()
                    {
                    ProgressDialog dialog;

                    @Override protected void onPreExecute()
                        {
                        dialog = new ProgressDialog(getActivity());
                        dialog.setMessage(message);
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(false);
                        dialog.show();
                        }

                    @Override protected Void doInBackground(Object... params)
                        {
                        backgroundWorker.run();
                        return null;
                        }

                    @Override protected void onPostExecute(Void aVoid)
                        {
                        dialog.dismiss();
                        if (runPostOnUIThread != null)
                            {
                            runPostOnUIThread.run();
                            }
                        }
                    }.execute();
                }
            });
        }

    //----------------------------------------------------------------------------------------------
    // Progress Dialog remoting
    //----------------------------------------------------------------------------------------------

    public void showProgress(UILocation uiLocation, final String message, final double fractionComplete)
        {
        showProgress(uiLocation, message, ProgressParameters.fromFraction(fractionComplete));
        }
    public void showProgress(UILocation uiLocation, final String message, final double fractionComplete, int max)
        {
        showProgress(uiLocation, message, ProgressParameters.fromFraction(fractionComplete, max));
        }
    public void showProgress(UILocation uiLocation, final String message, ProgressParameters progressParameters)
        {
        showProgress(uiLocation, this.getActivity(), message, progressParameters);
        }

    public void showProgress(UILocation uiLocation, final Activity activity, final String message, final ProgressParameters progressParameters)
        {
        final int maxMax = 10000;   // per ProgressBar.MAX_LEVEL
        final int cappedMax = Math.min(progressParameters.max, maxMax);

        this.runOnUiThread(new Runnable()
            {
            @Override public void run()
                {
                if (currentProgressDialog == null)
                    {
                    currentProgressDialog = new ProgressDialog(activity);
                    currentProgressDialog.setMessage(message);
                    currentProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    currentProgressDialog.setMax(cappedMax);
                    currentProgressDialog.setProgress(0);
                    currentProgressDialog.setCanceledOnTouchOutside(false);
                    currentProgressDialog.show();
                    }
                currentProgressDialog.setProgress(progressParameters.cur);
                }
            });

        if (uiLocation == UILocation.BOTH)
            {
            RobotCoreCommandList.ShowProgress showProgress = new RobotCoreCommandList.ShowProgress();
            showProgress.message = message;
            showProgress.cur = progressParameters.cur;
            showProgress.max = progressParameters.max;
            NetworkConnectionHandler.getInstance().sendCommand(new Command(RobotCoreCommandList.CMD_SHOW_PROGRESS, showProgress.serialize()));
            }
        }

    public void dismissProgress(UILocation uiLocation)
        {
        this.runOnUiThread(new Runnable()
            {
            @Override public void run()
                {
                if (currentProgressDialog != null)
                    {
                    currentProgressDialog.dismiss();
                    currentProgressDialog = null;
                    }
                }
            });

        if (uiLocation == UILocation.BOTH)
            {
            NetworkConnectionHandler.getInstance().sendCommand(new Command(RobotCoreCommandList.CMD_DISMISS_PROGRESS));
            }
        }

    //----------------------------------------------------------------------------------------------
    // Alert Dialog
    //----------------------------------------------------------------------------------------------

    public static class AlertDialogContext
        {
        public final AlertDialog dialog;
        public boolean isArmed = true;
        public CountDownLatch dismissed = new CountDownLatch(1);

        public AlertDialogContext(AlertDialog dialog)
            {
            this.dialog = dialog;
            }
        }

    public AlertDialogContext showAlertDialog(UILocation uiLocation, String title, String message)
        {
        return showAlertDialog(uiLocation, getActivity(), title, message);
        }
    public synchronized AlertDialogContext showAlertDialog(final UILocation uiLocation, final Activity activity, final String title, final String message)
        {
        dismissAlertDialog(uiLocation); // only one alert at a time
        Assert.assertNull(currentAlertDialog);

        this.synchronousRunOnUiThread(new Runnable()
            {
            @Override public void run()
                {
                if (currentAlertDialog == null)
                    {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(title);
                    builder.setMessage(message);
                    builder.setNeutralButton(R.string.buttonNameOK, null);
                    currentAlertDialog = new AlertDialogContext(builder.create());
                    currentAlertDialog.dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                        {
                        final AlertDialogContext alertDialogContext = currentAlertDialog; // make sure we operate on the correct state
                        @Override public void onDismiss(DialogInterface dialog)
                            {
                            RobotLog.vv(TAG, "alert dialog dismissed: 0x%08x", alertDialogContext.hashCode());
                            alertDialogContext.dismissed.countDown();
                            if (alertDialogContext.isArmed)
                                {
                                // Actively dismissing on the DS should also dismiss on the RC, and visa versa
                                dismissAlertDialog(uiLocation==UILocation.ONLY_LOCAL ? UILocation.ONLY_LOCAL : UILocation.BOTH);
                                }
                            }
                        });
                    currentAlertDialog.dialog.show();
                    }
                }
            });

        AlertDialogContext result = currentAlertDialog;
        Assert.assertNotNull(result);

        if (uiLocation==UILocation.BOTH)
            {
            RobotCoreCommandList.ShowAlert showAlert = new RobotCoreCommandList.ShowAlert();
            showAlert.title = title;
            showAlert.message = message;
            NetworkConnectionHandler.getInstance().sendCommand(new Command(RobotCoreCommandList.CMD_SHOW_ALERT, showAlert.serialize()));
            }

        return result;
        }

    public void dismissAlertDialog(UILocation uiLocation)
        {
        this.runOnUiThread(new Runnable()
            {
            @Override public void run()
                {
                if (currentAlertDialog != null)
                    {
                    currentAlertDialog.isArmed = false;
                    currentAlertDialog.dialog.dismiss();
                    currentAlertDialog = null;
                    }
                }
            });

        if (uiLocation==UILocation.BOTH)
            {
            NetworkConnectionHandler.getInstance().sendCommand(new Command(RobotCoreCommandList.CMD_DISMISS_ALERT));
            }
        }

    //----------------------------------------------------------------------------------------------
    // Toast
    //----------------------------------------------------------------------------------------------

    /**
     * Displays a toast message to the user. May be called from any thread.
     */
    public void showToast(UILocation uiLocation, String msg)
        {
        showToast(uiLocation, getActivity(), getApplication(), msg);
        }
    public void showToast(UILocation uiLocation, String msg, int duration)
        {
        showToast(uiLocation, getActivity(), getApplication(), msg, duration);
        }
    public void showToast(UILocation uiLocation, Context context, String msg )
        {
        showToast(uiLocation, getActivity(), context, msg);
        }
    public void showToast(UILocation uiLocation, final Activity activity, Context context, String msg)
        {
        showToast(uiLocation, activity, context, msg, Toast.LENGTH_SHORT);
        }

    public void showToast(UILocation uiLocation, final Activity activity, final Context context, final String msg, final int duration)
        {
        activity.runOnUiThread(new Runnable()
            {
            @Override public void run()
                {
                Toast toast = Toast.makeText(context, msg, duration);
                TextView message = (TextView) toast.getView().findViewById(android.R.id.message);
                message.setTextColor(Color.WHITE);
                message.setTextSize(18);
                toast.show();
                }
            });

        if (uiLocation==UILocation.BOTH)
            {
            RobotCoreCommandList.ShowToast showToast = new RobotCoreCommandList.ShowToast();
            showToast.message = msg;
            showToast.duration = duration;
            NetworkConnectionHandler.getInstance().sendCommand(new Command(RobotCoreCommandList.CMD_SHOW_TOAST, showToast.serialize()));
            }
        }

    //----------------------------------------------------------------------------------------------
    // Activities
    //----------------------------------------------------------------------------------------------

    /**
     * Returns the contextually running {@link Activity}
     * @return the contextually running {@link Activity}
     */
    public Activity getActivity()
        {
        return currentActivity;
        }

    /**
     * Returns the root activity of the current application
     * @return the root activity of the current application
     */
    public Activity getRootActivity()
        {
        return rootActivity;
        }

    private void initializeRootActivityIfNecessary()
        {
        if (rootActivity == null)
            {
            rootActivity = currentActivity;
            RobotLog.vv(TAG, "rootActivity=%s", rootActivity.getClass().getSimpleName());
            }
        }

    /**
     * {@link LifeCycleMonitor} is a class that allows us to keep track of the currently active Activity.
     */
    private class LifeCycleMonitor implements Application.ActivityLifecycleCallbacks
        {
        @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState)
            {
            currentActivity = activity;
            initializeRootActivityIfNecessary();
            }

        @Override public void onActivityStarted(Activity activity)
            {
            currentActivity = activity;
            initializeRootActivityIfNecessary();
            }

        @Override public void onActivityResumed(Activity activity)
            {
            currentActivity = activity;
            initializeRootActivityIfNecessary();
            }

        @Override public void onActivityPaused(Activity activity)
            {
            }

        @Override public void onActivityStopped(Activity activity)
            {
            }

        @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState)
            {
            }

        @Override public void onActivityDestroyed(Activity activity)
            {
            if (activity == rootActivity && rootActivity != null)
                {
                RobotLog.vv(TAG, "rootActivity=%s destroyed", rootActivity.getClass().getSimpleName());
                rootActivity = null;
                }
            }
        }
    }
