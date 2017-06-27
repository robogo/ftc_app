package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by Dan on 6/7/2017.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "SensorTest", group = "Ryan")
public class SensorInput extends LinearOpMode{

    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor lightSensor;
    GyroSensor gyroSensor;
    TouchSensor touchSensor;



    @Override
    public void runOpMode(){


        robot.init(hardwareMap);
        lightSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        lightSensor.enableLed(true);
        gyroSensor = hardwareMap.gyroSensor.get("gyro_sensor");
        gyroSensor.calibrate();
        touchSensor = hardwareMap.touchSensor.get("touch_sensor");
while(opModeIsActive() && !gyroSensor.isCalibrating())
        if (touchSensor.isPressed()) {
            telemetry.addData("You are this strong:", touchSensor.getValue());
            telemetry.update();
        }
        if (lightSensor.getLightDetected() > 0.2){
            telemetry.addData("There's this much light:", lightSensor.getLightDetected());
            telemetry.update();
        }
        if (gyroSensor.getRotationFraction() > 0.0) {

            telemetry.addData("I'm getting dizzy...", gyroSensor.getRotationFraction());
            telemetry.update();
        }
    };
}
