//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qualcomm.robotcore.wifi;

import android.content.Context;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.NetworkConnection;
import com.qualcomm.robotcore.wifi.NetworkType;
import com.qualcomm.robotcore.wifi.SoftApAssistant;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;

public class NetworkConnectionFactory {
    public static final String NETWORK_CONNECTION_TYPE = "NETWORK_CONNECTION_TYPE";

    public NetworkConnectionFactory() {
    }

    public static NetworkConnection getNetworkConnection(NetworkType type, Context context) {
        RobotLog.v("Getting network assistant of type: " + type);
        switch(type) {
            case WIFIDIRECT:
                return WifiDirectAssistant.getWifiDirectAssistant(context);
            case LOOPBACK:
                return null;
            case SOFTAP:
                return SoftApAssistant.getSoftApAssistant(context);
            case WIFI:
                return WifiNetworkConnection.getWifiConnection(context);
            default:
                return null;
        }
    }

    public static NetworkType getTypeFromString(String type) {
        return NetworkType.fromString(type);
    }
}
