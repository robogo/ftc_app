package com.robogo;

import com.qualcomm.robotcore.hardware.LightSensor;

public class MockLightSensor extends SensorMock implements LightSensor {

    public MockLightSensor(EmulatedHardwareFactory factory) {
        super(factory);
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "light_sensor";
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
        return getData();
    }

    @Override
    public double getRawLightDetected() {
        return getData();
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
