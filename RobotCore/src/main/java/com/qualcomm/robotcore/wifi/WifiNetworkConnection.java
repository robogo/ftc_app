//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qualcomm.robotcore.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.qualcomm.robotcore.robocol.PeerDiscoveryManager;
import com.qualcomm.robotcore.robocol.RobocolDatagramSocket;
import com.qualcomm.robotcore.util.Network;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.internal.network.NetworkConnectionHandler;
import org.firstinspires.ftc.robotcore.internal.network.RecvLoopRunnable;
import org.firstinspires.ftc.robotcore.internal.network.SocketConnect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class WifiNetworkConnection extends NetworkConnection {
    private static final int PORT = 45671;
    private static WifiNetworkConnection wifiConnection = null;
    private final List<ScanResult> scanResults = new ArrayList();
    private Context context;
    private WifiManager wifi;
    private NetworkConnectionCallback callback;
    private InetAddress remote;

    public static synchronized NetworkConnection getWifiConnection(Context context) {
        if(wifiConnection == null) {
            wifiConnection = new WifiNetworkConnection(context);
        }
        return wifiConnection;
    }

    private WifiNetworkConnection(Context context) {
        this.callback = null;
        this.context = context;
        this.wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.start();
    }

    public List<ScanResult> getScanResults() {
        return this.scanResults;
    }

    public NetworkType getNetworkType() {
        return NetworkType.WIFI;
    }

    public void enable() {
    }

    public void disable() {
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
        return remote;
    }

    public String getConnectionOwnerName() {
        return "";
    }

    public String getConnectionOwnerMacAddress() {
        return "";
    }

    public boolean isConnected() {
        return this.remote != null;
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
        return "unknown";
    }

    public String getPassphrase() {
        return "";
    }

    public ConnectStatus getConnectStatus() {
        return remote != null ? ConnectStatus.CONNECTED : ConnectStatus.NOT_CONNECTED;
    }

    private InetAddress getBroadcastAddress() throws UnknownHostException {
        DhcpInfo dhcp = wifi.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        RobotLog.i("My address %s broadcast address %s", getIpAddressAsString(dhcp.ipAddress), getIpAddressAsString(broadcast));
        return getAddress(broadcast);
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buf = new byte[64];
                    RobotLog.i("Waiting for message from driver station console");
                    DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
                    socket.setBroadcast(true);
                    DatagramPacket dgp = new DatagramPacket(buf, buf.length);
                    socket.receive(dgp);
                    remote = dgp.getAddress();
                    RobotLog.i("Received packet from " + remote.toString());
                    socket.close();
                    if (callback != null) {
                        callback.onNetworkConnectionEvent(Event.CONNECTION_INFO_AVAILABLE);
                    }
                } catch (SocketException e) {
                    RobotLog.e("Failed to open socket: " + e.toString());
                } catch (IOException e) {
                    RobotLog.e("Failed to receive: " + e.toString());
                }
            }
        }).start();
    }
}
