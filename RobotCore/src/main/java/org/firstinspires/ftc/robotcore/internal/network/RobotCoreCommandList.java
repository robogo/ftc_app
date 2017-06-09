/*
Copyright (c) 2016-2017 Robert Atkinson

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
package org.firstinspires.ftc.robotcore.internal.network;

import com.google.gson.Gson;

import org.firstinspires.ftc.robotcore.internal.ProgressParameters;

import java.io.File;

/**
 * {@link RobotCoreCommandList} contains network commands that are accessible in the RobotCore module
 */
public class RobotCoreCommandList
    {
    //----------------------------------------------------------------------------------------------
    // User interface remoting
    //----------------------------------------------------------------------------------------------

    public static final String CMD_SHOW_TOAST = "CMD_SHOW_TOAST";
    static public class ShowToast
        {
        public int     duration;
        public String  message;

        public String serialize()
            {
            return new Gson().toJson(this);
            }
        public static ShowToast deserialize(String serialized)
            {
            return new Gson().fromJson(serialized, ShowToast.class);
            }
        }

    public static final String CMD_SHOW_PROGRESS = "CMD_SHOW_PROGRESS";
    static public class ShowProgress extends ProgressParameters
        {
        public String message;

        public String serialize()
            {
            return new Gson().toJson(this);
            }
        public static ShowProgress deserialize(String serialized)
            {
            return new Gson().fromJson(serialized, ShowProgress.class);
            }
        }

    public static final String CMD_DISMISS_PROGRESS = "CMD_DISMISS_PROGRESS";

    public static final String CMD_SHOW_ALERT = "CMD_SHOW_ALERT";
    static public class ShowAlert
        {
        public String title;
        public String message;

        public String serialize()
            {
            return new Gson().toJson(this);
            }
        public static ShowAlert deserialize(String serialized)
            {
            return new Gson().fromJson(serialized, ShowAlert.class);
            }
        }
    public static final String CMD_DISMISS_ALERT = "CMD_DISMISS_ALERT";

    public static final String CMD_REQUEST_INSPECTION_REPORT = "CMD_REQUEST_INSPECTION_REPORT";
    public static final String CMD_REQUEST_INSPECTION_REPORT_RESP = "CMD_REQUEST_INSPECTION_REPORT_RESP";

    //----------------------------------------------------------------------------------------------
    // Robot semantics and management
    //----------------------------------------------------------------------------------------------

    public static final String CMD_INIT_OP_MODE_RESP = "CMD_INIT_OP_MODE_RESP";

    public static final String CMD_RUN_OP_MODE_RESP = "CMD_RUN_OP_MODE_RESP";

    public static final String CMD_REQUEST_USER_DEVICE_LIST_RESP = "CMD_REQUEST_USER_DEVICE_LIST_RESP";

    // Used for sending a (pref,value) pair either from a RC to a DS or the other way around.
    // The pair is always a setting of the robot controller. When sent to the RC, it is a request
    // to update the setting; when sent from the RC, it is an announcement of the current value
    // of the setting.
    public static final String CMD_ROBOT_CONTROLLER_PREFERENCE = "CMD_ROBOT_CONTROLLER_PREFERENCE";

    //----------------------------------------------------------------------------------------------
    // Wifi management
    //----------------------------------------------------------------------------------------------

    // Reports that the list of wifi direct remembered groups has changed
    public static final String CMD_WIFI_DIRECT_REMEMBERED_GROUPS_CHANGED = "CMD_WIFI_DIRECT_REMEMBERED_GROUPS_CHANGED";

    public static final String CMD_DISCONNECT_FROM_WIFI_DIRECT = "CMD_DISCONNECT_FROM_WIFI_DIRECT";

    //----------------------------------------------------------------------------------------------
    // Update management
    //----------------------------------------------------------------------------------------------

    /**
     * For the moment (perhaps forever), firmware images can only either be files or assets
     */
    public static class FWImage
        {
        public File file;
        public boolean isAsset;

        public FWImage(File file, boolean isAsset)
            {
            this.file = file;
            this.isAsset = isAsset;
            }

        public String getName()
            {
            return file.getName();
            }
        }
    }
