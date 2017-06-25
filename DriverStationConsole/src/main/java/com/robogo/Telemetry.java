package com.robogo;

import java.nio.ByteBuffer;

public class Telemetry extends Frame {
    private StringMap strings;
    private NumberMap numbers;
    private long timestamp;
    private byte robotState;
    private String tag;

    @Override
    public byte type() {
        return Frame.TELEMETRY;
    }

    public Telemetry(byte[] bytes, int len) {
        strings = new StringMap();
        numbers = new NumberMap();

        ByteBuffer buffer = getReadBuffer(bytes, len);
        timestamp = buffer.getLong();
        boolean isSorted = buffer.get() != 0;
        robotState = buffer.get();
        tag = readString(buffer, 1);

        // data strings
        int count = ubyteToInt(buffer.get());
        for (int i = 0; i < count; i++) {
            String key = readString(buffer);
            String val = readString(buffer);
            strings.put(key, val);
        }

        // data numbers
        count = ubyteToInt(buffer.get());
        for (int i = 0; i < count; i++) {
            String key = readString(buffer);
            float val = buffer.getFloat();
            numbers.put(key, val);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte getRobotState() {
        return robotState;
    }

    public String getTag() {
        return tag;
    }

    public StringMap strings() {
        return strings;
    }

    public NumberMap numbers() {
        return numbers;
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
