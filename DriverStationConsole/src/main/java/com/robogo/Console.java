package com.robogo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Console {
    public static void main(String[] args) {
        DriverStation ds = new DriverStation();
        ds.start();
        System.console().readLine();
        ds.stop();
    }
}
