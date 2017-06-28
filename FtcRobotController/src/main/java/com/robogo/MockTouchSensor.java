package com.robogo;

import com.qualcomm.robotcore.hardware.TouchSensor;

public class MockTouchSensor extends Mock implements TouchSensor {
    @Override
    public double getValue() {
        return getData();
    }

    @Override
    public boolean isPressed() {
        return getValue() > 0;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "touch_sensor";
    }

    @Override
    public String getConnectionInfo() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}
