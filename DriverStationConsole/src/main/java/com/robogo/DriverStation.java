package com.robogo;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class DriverStation {

    public interface EventHandler {
        void onOpModeList(String[] opModes);
        void OnTelemetry(Telemetry tele);
        void onLog(int level, String format, Object... args);
    }

    public static final int PORT =45671;
    public static final int FTC_PORT = 20884;
    public static final int OUTPUT = 0;
    public static final int INFO = 1;
    public static final int DEBUG = 2;
    private DatagramSocket socket;
    private InetAddress remoteAddress;
    private int remotePort;
    private EventHandler handler;
    private boolean stopped;
    private long lastDiscovery;
    private long lastPacket;
    private String opModes;

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }

    public void start(String address) {
        try {
            InetAddress ip;
            if (address != null) {
                ip = InetAddress.getByName(address);
            } else {
                DatagramSocket test = new DatagramSocket();
                test.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip = test.getLocalAddress();
                test.close();
            }
            announce(ip);
            listen(ip);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (socket != null) {
            socket.close();
        }
        stopped = true;
    }

    public void changeOpMode(String action, String name) {
        if (socket == null || remoteAddress == null) {
            return;
        }
        String cmdName = null;
        if (action.equals("INIT")) {
            cmdName = Command.CMD_INIT_OP_MODE;
        } else if (action.equals("START")) {
            cmdName = Command.CMD_RUN_OP_MODE;
        } else if (action.equals("STOP")) {
            cmdName = Command.CMD_INIT_OP_MODE;
            name = Command.DEFAULT_OP_MODE_NAME;
        }
        if (cmdName != null) {
            Command cmd = new Command(cmdName, name);
            try {
                int size = sendFrame(socket, remoteAddress, remotePort, cmd);
                log(INFO, "send %s (%d) %s sn:%d, ts:%d, ack:%d", remoteAddress.toString(),
                        size, cmd.name(), cmd.seqNum(), cmd.timestamp(), cmd.acknowledged());
            } catch (IOException e) {
                e.printStackTrace();
                log(INFO, "Send OpMode command filed %s:%s", e.getCause(), e.getMessage());
            }
        }
    }

    private void announce(final InetAddress ip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket broadcast = null;
                try {
                    broadcast = new DatagramSocket(PORT);
                    broadcast.setBroadcast(true);
                    InetAddress address = getBroadcast(ip);
                    byte[] buf = new byte[4];
                    while (!Thread.currentThread().isInterrupted() && !stopped) {
                        if (System.currentTimeMillis() - lastPacket > 2000) {
                            log(INFO, "Broadcasting to " + address.toString());
                            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, PORT);
                            broadcast.send(sendPacket);
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
                if (broadcast != null) {
                    broadcast.close();
                }
            }
        }).start();
    }

    private void listen(final InetAddress ip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                log(INFO, "Listening on " + ip.toString());
                try {
                    socket = new DatagramSocket(FTC_PORT);
                    byte[] buf = new byte[1024 * 8];
                    while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);
                        remoteAddress = packet.getAddress();
                        remotePort = packet.getPort();
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
                if (socket != null) {
                    socket.close();
                }
            }
        }).start();
    }

    private void handleBuffer(DatagramSocket socket, DatagramPacket packet, byte[] buf) throws IOException {
        byte  messageType = packet.getData()[0];
        switch (messageType) {
            case Frame.EMPTY:
                log(INFO, "recv Empty");
                break;
            case Frame.HEARTBEAT:
                handleHeartbeat(socket, packet);
                break;
            case Frame.GAMEPAD:
                handleGamepad(socket, packet);
                break;
            case Frame.PEER_DISCOVERY:
                handlePeerDiscovery(socket, packet);
                break;
            case Frame.COMMAND:
                handleCommand(socket, packet);
                break;
            case Frame.TELEMETRY:
                handleTelemetry(socket, packet);
                break;
            default:
                log(INFO, "recv %s (%d) Unknown type:%d", packet.getAddress().toString(), packet.getLength(), messageType);
        }
    }

    private void handlePeerDiscovery(DatagramSocket socket, DatagramPacket packet) {
        ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 1, packet.getLength() - 1);
        byte  version = buffer.get();
        short payload = buffer.getShort();
        byte  type = buffer.get();
        short sn = buffer.getShort();
        log(INFO, "recv %s (%d) PeerDiscovery ver:%d type:%d, sn:%d", packet.getAddress().toString(),
                packet.getLength(), version, type, sn);
        if (System.currentTimeMillis() - lastDiscovery >= 1000) {
            DatagramPacket reply = new DatagramPacket(packet.getData(),
                    packet.getLength(), packet.getAddress(), packet.getPort());
            try {
                socket.send(reply);
                lastDiscovery = System.currentTimeMillis();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleHeartbeat(DatagramSocket socket, DatagramPacket packet) {
        log(INFO, "recv %s (%d) Heartbeat", packet.getAddress().toString(), packet.getLength());
    }

    private void handleGamepad(DatagramSocket socket, DatagramPacket packet) {
        log(INFO, "recv %s (%d) Gamepad", packet.getAddress().toString(), packet.getLength());
    }

    private void handleCommand(DatagramSocket socket, DatagramPacket packet) throws IOException {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        Command cmd = new Command(packet.getData(), packet.getLength());
        log(INFO, "recv %s (%d) %s sn:%d, ts:%d, ack:%d, extra:%s", address.toString(), packet.getLength(),
                cmd.name(), cmd.seqNum(), cmd.timestamp(), cmd.acknowledged(), cmd.body());
        if (cmd.acknowledged() == 0) {
            Command ack = cmd.getAck();
            int size = sendFrame(socket, address, port, ack);
            log(INFO, "send %s (%d) %s sn:%d, ts:%d, ack:%d", address.toString(),
                    size, ack.name(), ack.seqNum(), ack.timestamp(), ack.acknowledged());
        } else {
            return;
        }
        if (opModes == null) {
            Command req = new Command(Command.CMD_REQUEST_OP_MODE_LIST, "");
            int size = sendFrame(socket, packet.getAddress(), packet.getPort(), req);
            opModes = "";
            log(INFO, "send %s (%d) %s sn:%d, ts:%d, ack:%d", packet.getAddress().toString(),
                    size, req.name(), req.seqNum(), req.timestamp(), req.acknowledged());
        } else if (cmd.name().equals(Command.CMD_REQUEST_OP_MODE_LIST_RESP)) {
            opModes = cmd.body();
            log(OUTPUT, "OpMode list: %s", opModes);
            if (handler != null) {
                Frame.Collection collection = Frame.stringToObj(opModes, Frame.Collection.class);
                String[] list = new String[collection.size()];
                for (int i = 0; i < list.length; i++) {
                    list[i] = collection.get(i).get("name");
                }
                handler.onOpModeList(list);
            }
        }
    }

    private void handleTelemetry(DatagramSocket socket, DatagramPacket packet) {
        Telemetry tele = new Telemetry(packet.getData(), packet.getLength());
        log(INFO, "recv %s (%d) Telemetry: ts %d robot %d tag %s strings %s numbers %s",
                packet.getAddress().toString(),
                packet.getLength(),
                tele.getTimestamp(),
                tele.getRobotState(),
                tele.getTag(),
                new Gson().toJson(tele.strings()),
                new Gson().toJson(tele.numbers()));
        if (handler != null) {
            handler.OnTelemetry(tele);
        }
    }

    private int sendFrame(DatagramSocket socket, InetAddress address, int port, Frame frame) throws IOException {
        byte[] bytes = frame.serialize();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
        socket.send(packet);
        return bytes.length;
    }

    private void log(int level, String format, Object... args) {
        if (handler != null) {
            handler.onLog(level, format, args);
        }
    }

    private InetAddress getBroadcast(InetAddress ip) throws UnknownHostException, SocketException {
        InetAddress result = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ip);
        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            int prefix = address.getNetworkPrefixLength();
            if (prefix <= 32) {
                int netmask = 0xFFFFFFFF;
                for (int i = 0; i < (32 - prefix); i++) {
                    netmask &= ~(1 << i);
                }
                int ip2 = ByteBuffer.wrap(ip.getAddress()).getInt();
                int broadcast = (ip2 & netmask) | ~netmask;
                result = InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(broadcast).array());
                log(INFO, "ip %s(%d) prefix %d netmask %X broadcast %s(%d)",
                        ip.toString(), ip2, prefix, netmask, result.toString(), broadcast);
            }
        }
        return result;
    }
}
