package com.robogo;

import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;

import static com.qualcomm.robotcore.hardware.IrSeekerSensor.Mode.MODE_1200HZ;

/**
 * Created by xinchen on 7/23/2017.
 */

public class MockIRseeker extends SensorMock implements IrSeekerSensor {

    public MockIRseeker(EmulatedHardwareFactory factory) {
        super(factory);
    }

    boolean isAngle;

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "sensor_IR";
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
    public void setSignalDetectedThreshold(double threshold) {

    }

    @Override
    public double getSignalDetectedThreshold() {
        return 25;
    }

    @Override
    public void setMode(Mode mode) {

    }

    @Override
    public Mode getMode() {
        return MODE_1200HZ;
    }

    @Override
    public boolean signalDetected() {
        return true;
    }

    public void setIsGetDataGettingAngle(boolean boo) {
        isAngle = boo;
    }

    @Override
    public double getAngle() {
        if (isAngle == true) {
            return getData();
        } else {
            return 180;
        }
    }

    @Override
    public double getStrength() {
        if (isAngle == false) {
            return getData();
        } else {
            return 0;
        }
    }

    @Override
    public IrSeekerIndividualSensor[] getIndividualSensors() {
        return new IrSeekerIndividualSensor[0];
    }

    @Override
    public void setI2cAddress(I2cAddr newAddress) {

    }

    @Override
    public I2cAddr getI2cAddress() {
        return null;
    }
}
