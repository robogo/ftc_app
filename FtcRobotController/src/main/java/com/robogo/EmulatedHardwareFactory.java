package com.robogo;

import android.content.Context;

import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

public class EmulatedHardwareFactory extends HardwareFactory{
    private Context       context;

    public EmulatedHardwareFactory(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public HardwareMap createHardwareMap(EventLoopManager manager) throws RobotCoreException, InterruptedException {
        RobotLog.i("EmulatedHardwareFactory.createHardwareMap()");
        HardwareMap map = new HardwareMap(context);
        map.dcMotorController.put("motor_controller", new MockDcMotorController());
        map.dcMotor.put("left_drive", new MockDcMotor());
        map.dcMotor.put("right_drive", new MockDcMotor());
        map.dcMotor.put("left_arm", new MockDcMotor());
        map.dcMotor.put("right_arm", new MockDcMotor());
        map.servo.put("left_hand", new MockServo());
        map.servo.put("right_hand", new MockServo());
        map.lightSensor.put("sensor_light", new MockLightSensor());
        map.touchSensor.put("sensor_touch", new MockTouchSensor());
        map.opticalDistanceSensor.put("sensor_ods", new MockOpticalDistanceSensor());

        return map;
    }
}
