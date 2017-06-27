package com.robogo;

import java.nio.ByteBuffer;

public class Command extends Frame {
    public static final String CMD_REQUEST_OP_MODE_LIST = "CMD_REQUEST_OP_MODE_LIST";
    public static final String CMD_REQUEST_OP_MODE_LIST_RESP = "CMD_REQUEST_OP_MODE_LIST_RESP";
    public static final String CMD_INIT_OP_MODE = "CMD_INIT_OP_MODE";
    public static final String CMD_RUN_OP_MODE = "CMD_RUN_OP_MODE";
    public static final String DEFAULT_OP_MODE_NAME = "$Stop$Robot$";

    private long timestamp;
    private byte acknowledged;
    private String name;
    private String body;

    private Command() {
    }

    public Command(String name, String body) {
        this.setSeqNum();
        this.name = name;
        this.body = body;
        this.timestamp = System.nanoTime();
    }

    public Command(byte[] bytes, int len) {
        ByteBuffer buffer = getReadBuffer(bytes, len);
        timestamp = buffer.getLong();
        acknowledged = buffer.get();
        name = readString(buffer);
        body = "";
        if (acknowledged == 0) {
            body = readString(buffer);
        }
    }

    public Command getAck() {
        Command ack = new Command();
        ack.name = name;
        ack.timestamp = timestamp;
        ack.acknowledged = 1;
        ack.body = "";
        return ack;
    }

    public long timestamp() {
        return timestamp;
    }

    public String name() {
        return name;
    }

    public String body() {
        return body;
    }

    public byte acknowledged() {
        return acknowledged;
    }

    @Override
    public byte type() {
        return Frame.COMMAND;
    }

    @Override
    public byte[] serialize() {
        byte[] nameBytes = Frame.stringToUtf8(name);
        byte[] bodyBytes = Frame.stringToUtf8(body);
        int size = 8 + 1 + 2 + nameBytes.length;
        if (acknowledged == 0) {
            size += 2 + bodyBytes.length;
        }
        ByteBuffer buffer = getWriteBuffer(size);
        buffer.putLong(timestamp);
        buffer.put(acknowledged);
        buffer.putShort((short)nameBytes.length);
        buffer.put(nameBytes);
        if (acknowledged == 0) {
            buffer.putShort((short)bodyBytes.length);
            buffer.put(bodyBytes);
        }
        return buffer.array();
    }
}
