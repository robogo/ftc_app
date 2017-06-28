/*
 * Copyright (c) 2015 Qualcomm Technologies Inc
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
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
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

package com.qualcomm.robotcore.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.qualcomm.robotcore.util.Network;
import com.qualcomm.robotcore.util.RobotLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DeviceNetworkConnection extends NetworkConnection {
    private static final int PORT = 45671;
    private static DeviceNetworkConnection connection = null;
    private final List<ScanResult> scanResults = new ArrayList();
    private Context context;
    private WifiManager wifi;
    private NetworkConnectionCallback callback;
    private InetAddress remote;

    public static synchronized NetworkConnection getConnection(Context context) {
        if(connection == null) {
            connection = new DeviceNetworkConnection(context);
        }
        return connection;
    }

    private DeviceNetworkConnection(Context context) {
        this.callback = null;
        this.context = context;
        this.wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        RobotLog.i("WifiNetworkConnection starting...");
        this.start();
    }

    public List<ScanResult> getScanResults() {
        return this.scanResults;
    }

    public NetworkType getNetworkType() {
        return NetworkType.DEVICE;
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
                    for (InetAddress a : Network.getLocalIpAddresses()) {
                        RobotLog.i("Local address %s", a.toString());
                    }
                    byte[] buf = new byte[64];
                    RobotLog.i("Waiting for message from driver station console");
                    DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
                    socket.setBroadcast(true);
                    DatagramPacket dgp = new DatagramPacket(buf, buf.length);
                    socket.receive(dgp);
                    remote = dgp.getAddress();
                    RobotLog.i("Received packet from %s:%d", remote.toString(), dgp.getPort());
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
