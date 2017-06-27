package com.robogo;

import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Frame {
    public static final byte EMPTY = 0;
    public static final byte HEARTBEAT = 1;
    public static final byte GAMEPAD = 2;
    public static final byte PEER_DISCOVERY = 3;
    public static final byte COMMAND = 4;
    public static final byte TELEMETRY = 5;

    final static AtomicInteger nextSequenceNumber = new AtomicInteger();
    int seqNum;
    int payload;

    public int seqNum() {
        return this.seqNum;
    }

    public abstract byte type();

    public abstract  byte[] serialize();

    protected void setSeqNum() {
        this.seqNum = nextSequenceNumber.getAndIncrement();
    }

    protected ByteBuffer getWriteBuffer(int payload) {
        ByteBuffer buffer = ByteBuffer.allocate(5 + payload);
        buffer.put(this.type());
        buffer.putShort((short)payload);
        buffer.putShort((short)this.seqNum);
        return buffer;
    }

    protected ByteBuffer getReadBuffer(byte[] bytes, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, len);
        buffer.get();   // ignore type
        payload = buffer.getShort();
        seqNum = buffer.getShort();
        return buffer;
    }

    // serialization utilities
    public static class StringMap extends HashMap<String, String> {
    }

    public static class NumberMap extends HashMap<String, Float> {
    }

    public static class Collection extends ArrayList<StringMap> {
    }

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public static String utf8ToString(byte[] utf8String) {
        return new String(utf8String, UTF8_CHARSET);
    }

    public static byte[] stringToUtf8(String str) {
        return str.getBytes(UTF8_CHARSET);
    }

    public static int uhortToInt(short s) {
        return ((int)(s) & 0xffff);
    }

    public static int ubyteToInt(byte s) {
        return ((int)(s) & 0xff);
    }

    public static <T> T stringToObj(String str, Class<T> cls) {
        return new Gson().fromJson(str, cls);
    }

    public static String readString(ByteBuffer buffer) {
        return readString(buffer, 2);
    }

    public static String readString(ByteBuffer buffer, int width) {
        int cb = width == 2 ? uhortToInt(buffer.getShort()) : ubyteToInt(buffer.get());
        if (cb == 0) {
            return "";
        }
        byte[] nameBytes = new byte[cb];
        buffer.get(nameBytes);
        return  utf8ToString(nameBytes);
    }
}
