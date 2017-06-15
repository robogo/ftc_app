package com.robogo;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

public class MockOpticalDistanceSensor implements OpticalDistanceSensor {
    @Override
    public Manufacturer getManufacturer() {
        return null;
    }

    @Override
    public String getDeviceName() {
        return null;
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

    @Override
    public double getLightDetected() {
        return 0;
    }

    @Override
    public double getRawLightDetected() {
        return 0;
    }

    @Override
    public double getRawLightDetectedMax() {
        return 0;
    }

    @Override
    public void enableLed(boolean enable) {

    }

    @Override
    public String status() {
        return null;
    }
}
