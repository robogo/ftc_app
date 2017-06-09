//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qualcomm.robotcore.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.qualcomm.robotcore.util.RobotLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class WifiNetworkConnection extends NetworkConnection implements WifiAssistant.WifiAssistantCallback {
    private static WifiNetworkConnection wifiConnection = null;
    private final List<ScanResult> scanResults = new ArrayList();
    private final WifiAssistant assistant;
    private Context context;
    private WifiManager wifi;
    private NetworkConnectionCallback callback;
    private ConnectStatus status;
    private String faultReason;

    public static synchronized NetworkConnection getWifiConnection(Context context) {
        if(wifiConnection == null) {
            wifiConnection = new WifiNetworkConnection(context);
        }

        return wifiConnection;
    }

    private WifiNetworkConnection(Context context) {
        this.callback = null;
        this.context = context;
        this.assistant = new WifiAssistant(context, this);
        this.wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.status = ConnectStatus.NOT_CONNECTED;
    }

    public List<ScanResult> getScanResults() {
        return this.scanResults;
    }

    public NetworkType getNetworkType() {
        return NetworkType.WIFI;
    }

    public void enable() {
        this.assistant.enable();
    }

    public void disable() {
        this.assistant.disable();
    }

    public void setCallback(NetworkConnectionCallback callback) {
        RobotLog.v("setting NetworkConnection callback: " + callback);
        this.callback = callback;
    }

    public void discoverPotentialConnections() {
    }

    public void cancelPotentialConnections() {
    }

    public void createConnection() {
    }

    public void connect(String ssid, String password) {
    }

    public void connect(String ssid) {
        this.connect(ssid, "");
    }

    public InetAddress getConnectionOwnerAddress() {
        return null;
    }

    public String getConnectionOwnerName() {
        return "";
    }

    public String getConnectionOwnerMacAddress() {
        return "";
    }

    public boolean isConnected() {
        return this.status == ConnectStatus.CONNECTED;
    }

    public String getDeviceName() {
        return "device";
    }

    public String getInfo() {
        StringBuilder s = new StringBuilder();
        s.append("Name: ").append(this.getDeviceName());

        return s.toString();
    }

    private String getIpAddressAsString(int ipAddress) {
        StringBuilder s = new StringBuilder();
        s.append(Integer.valueOf(ipAddress & 255))
                .append('.')
                .append(Integer.valueOf(ipAddress >> 8 & 255))
                .append('.')
                .append(Integer.valueOf(ipAddress >> 16 & 255))
                .append('.')
                .append(Integer.valueOf(ipAddress >> 24 & 255));
        return s.toString();
    }

    private InetAddress getAddress(int address) throws UnknownHostException {
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((address >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    public String getFailureReason() {
        return faultReason;
    }

    public String getPassphrase() {
        return "";
    }

    public ConnectStatus getConnectStatus() {
        return status;
    }

    @Override
    public void wifiEventCallback(WifiAssistant.WifiState event) {
        this.status = event == WifiAssistant.WifiState.CONNECTED ?
            ConnectStatus.CONNECTED : ConnectStatus.NOT_CONNECTED;
    }
}
