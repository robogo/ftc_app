package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by Dan on 6/4/2017.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "LineFollower", group = "Ryan")
public class LineFollower extends LinearOpMode {

    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor lightSensor;

    static final double whiteThreshold = 0.2;
    static final double Speed = 0.5;


    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        lightSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        lightSensor.enableLed(true);



        this.waitForStart();




        robot.leftMotor.setPower(Speed);
        robot.rightMotor.setPower(Speed);

        while (opModeIsActive() && lightSensor.getLightDetected() < whiteThreshold) {
            telemetry.addData("Status", lightSensor.getLightDetected());
            telemetry.update();
        }

        while (opModeIsActive() && !isStopRequested()) {

            while (lightSensor.getLightDetected() < whiteThreshold) {
                robot.leftMotor.setPower(lightSensor.getLightDetected());
                robot.rightMotor.setPower(0);
            }

            while (lightSensor.getLightDetected() > whiteThreshold) {
                robot.rightMotor.setPower(lightSensor.getLightDetected());
                robot.leftMotor.setPower(0);
            }
        }
        robot.leftMotor.setPower(0.0);
        robot.rightMotor.setPower(0.0);
    }
}
