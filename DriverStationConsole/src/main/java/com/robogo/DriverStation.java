package com.robogo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class DriverStation {
    public static final int PORT =45671;
    public static final int FTC_PORT = 20884;
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private boolean stopped;
    private long lastDiscovery;
    private long lastPacket;

    public void start() {
        DatagramSocket socket = null;
        InetAddress ip = null;
        try {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress();
            socket.close();

            announce(ip);
            listen(ip);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stopped = true;
    }

    private void announce(final InetAddress ip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(PORT, ip);
                    socket.setBroadcast(true);
                    byte[] buf = new byte[4];
                    while (!Thread.currentThread().isInterrupted() && !stopped) {
                        if (System.currentTimeMillis() - lastPacket > 2000) {
                            System.out.println("Broadcasting to " + ip.toString());
                            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), PORT);
                            socket.send(sendPacket);
                        }
                        Thread.sleep(1000);
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (socket != null) socket.close();
            }
        }).start();
    }

    private void listen(final InetAddress ip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Listening on " + ip.toString());
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(FTC_PORT);
                    byte[] buf = new byte[1024 * 8];
                    while (!Thread.currentThread().isInterrupted() && !stopped) {
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);
                        lastPacket = System.currentTimeMillis();
                        handleBuffer(socket, packet, buf);
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (socket != null) socket.close();
            }
        }).start();
    }

    private void handleBuffer(DatagramSocket socket, DatagramPacket packet, byte[] buf) {
        byte  messageType = packet.getData()[0];
        switch (messageType) {
            case 3: //PEER_DISCOVERY(3),
                handlePeerDiscovery(socket, packet);
                break;
            case 4: //COMMAND(4),
                handleCommand(socket, packet);
                break;
            case 5: //TELEMETRY(5);
                handleTelemetry(socket, packet);
                break;
/*
            case 0: //EMPTY
                break;
            case 1: //HEARTBEAT(1),
                break;
            case 2: //GAMEPAD(2),
                break;
*/
            default:
                System.out.printf("recv %s (%d) Unknown type:%d\n", packet.getAddress().toString(), packet.getLength(), messageType);
        }
    }

    private void handlePeerDiscovery(DatagramSocket socket, DatagramPacket packet) {
        ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 1, packet.getLength() - 1);
        byte  version = buffer.get();
        short payload = buffer.getShort();
        byte  type = buffer.get();
        short sn = buffer.getShort();
        System.out.printf("recv %s (%d) PeerDiscovery ver:%d type:%d, sn:%d\n", packet.getAddress().toString(), packet.getLength(), version, type, sn);
        if (System.currentTimeMillis() - lastDiscovery < 1000) return;
        lastDiscovery = System.currentTimeMillis();
        DatagramPacket reply = new DatagramPacket(packet.getData(), packet.getLength(), packet.getAddress(), packet.getPort());
        try {
            socket.send(reply);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleHeartbeat(DatagramSocket socket, DatagramPacket packet) {
    }

    private void handleCommand(DatagramSocket socket, DatagramPacket packet) {
        ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 3, packet.getLength() - 3);
        short sn = buffer.getShort();
        long timestamp = buffer.getLong();
        byte acknowledged = buffer.get();

        int cbName = unsignedShortToInt(buffer.getShort());
        byte[] nameBytes = new byte[cbName];
        buffer.get(nameBytes);
        String name = utf8ToString(nameBytes);
        String extra = "";
        int pos = buffer.position();

        if (acknowledged == 0) {
            int cbExtra = unsignedShortToInt(buffer.getShort());
            byte[] extraBytes = new byte[cbExtra];
            buffer.get(extraBytes);
            extra = utf8ToString(extraBytes);
        }
        System.out.printf("recv %s (%d) %s sn:%d, ts:%d, ack:%d, extra:%s\n", packet.getAddress().toString(), packet.getLength(),
                name, sn, timestamp, acknowledged, extra);
        if (acknowledged == 0) {
            byte[] ackBytes = Arrays.copyOf(packet.getData(), pos);
            ackBytes[13] = 1;
            try {
                socket.send(new DatagramPacket(ackBytes, ackBytes.length, packet.getAddress(), packet.getPort()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleTelemetry(DatagramSocket socket, DatagramPacket packet) {
    }

    private static int unsignedShortToInt(short s) {
        return ((int)(s) & 0xffff);
    }

    private static String utf8ToString(byte[] utf8String) {
        return new String(utf8String, UTF8_CHARSET);
    }
}
