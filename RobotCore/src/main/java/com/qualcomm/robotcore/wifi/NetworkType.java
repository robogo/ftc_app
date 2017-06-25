//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qualcomm.robotcore.wifi;

public enum NetworkType {
    WIFIDIRECT,
    LOOPBACK,
    SOFTAP,
    WIFI,
    UNKNOWN_NETWORK_TYPE;

    private NetworkType() {
    }

    public static NetworkType fromString(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (Exception var2) {
            return UNKNOWN_NETWORK_TYPE;
        }
    }
}
