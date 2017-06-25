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

import com.vuforia.TrackableResult;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;

/**
 * {@link VuforiaTrackableImpl} is the system implementation of {@link VuforiaTrackable}
 */
class VuforiaTrackableImpl implements VuforiaTrackable
    {
    protected VuforiaTrackablesImpl trackables;
    protected int                   index;
    protected String                name;
    protected Listener              listener;
    protected Object                userData;

    protected final Object          locationLock = new Object();
    protected OpenGLMatrix          location;

    VuforiaTrackableImpl(VuforiaTrackablesImpl trackables, int index)
        {
        this.trackables = trackables;
        this.index = index;
        this.userData = null;
        this.location = null;
        this.name = null;
        this.listener = new VuforiaTrackableDefaultListener(this);
        getVuforiaTrackable().setUserData(this);
        }

    static VuforiaTrackableImpl getTrackable(com.vuforia.Trackable trackable)
        {
        return (VuforiaTrackableImpl)trackable.getUserData();
        }

    public static VuforiaTrackableImpl getTrackable(TrackableResult trackableResult)
        {
        return getTrackable(trackableResult.getTrackable());
        }

    @Override public synchronized void setListener(Listener listener)
        {
        // We *always* have a listener
        this.listener = listener==null ? new VuforiaTrackableDefaultListener(this) : listener;
        }

    @Override public synchronized Listener getListener()
        {
        return this.listener;
        }

    @Override public synchronized void setUserData(Object object)
        {
        this.userData = object;
        }

    @Override public synchronized Object getUserData()
        {
        return this.userData;
        }

    @Override public VuforiaTrackables getTrackables()
        {
        return trackables;
        }

    @Override public void setLocation(OpenGLMatrix location)
        {
        /** Separate lock so as to accommodate upcalls from {@link VuforiaTrackableDefaultListener}  */
        synchronized (this.locationLock)
            {
            this.location = location;
            }
        }

    @Override public OpenGLMatrix getLocation()
        {
        synchronized (this.locationLock)
            {
            return this.location;
            }
        }

    @Override public String getName()
        {
        return this.name;
        }

    @Override public void setName(String name)
        {
        this.name = name;
        }

    public com.vuforia.Trackable getVuforiaTrackable()
        {
        return trackables.dataSet.getTrackable(this.index);
        }

    synchronized void noteNotTracked()
        {
        this.getListener().onNotTracked();
        }

    synchronized void noteTracked(TrackableResult trackableResult)
        {
        this.getListener().onTracked(trackableResult);
        }
    }
