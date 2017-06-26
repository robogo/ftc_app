package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
//import com.robogo.Mock;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

import java.io.IOException;

@Autonomous(name = "HelloRobot", group = "Test")
public class HelloRobot extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor lineSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        EnvLight stuff = new EnvLight();
        lineSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        lineSensor.enableLed(true);
        robot.rightClaw.setPosition(Servo.MIN_POSITION);
        //((Mock)lineSensor).setData(new double[]{5, 5.5}, new double[]{0.5, 0});
        double[] values = null;
        try {
            values = EnvLight.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        double tape = (values[1] + values[0] )/ 2;
        boolean seenTape = false;
        boolean positionedCorrectly = false;

        waitForStart();
        lineSensor.resetDeviceConfigurationForOpMode();//debug only
        while (opModeIsActive()) {
            // read current value
            double lightLevel = lineSensor.getLightDetected();
            telemetry.addData("light", lightLevel);

            // if not over line, go forward with 1.0
            robot.leftMotor.setPower(1);
            robot.rightMotor.setPower(1);
            try {
                if (lightLevel > tape && stuff.isTapeColorWhite()) {
                    seenTape = true;
                    robot.leftMotor.setPower(0);
                    robot.rightMotor.setPower(0);
                    telemetry.addData("seen tape", 1);
                    sleep(1000);
                    if (lineSensor.getLightDetected() > tape) {
                        positionedCorrectly = true;
                        telemetry.addData("arrived", 1);
                    }
                }else if (lightLevel < tape && stuff.isTapeColorWhite() == false) {
                    seenTape = true;
                    robot.leftMotor.setPower(0);
                    robot.rightMotor.setPower(0);
                    telemetry.addData("seen tape", 1);
                    sleep(1000);
                    if (lineSensor.getLightDetected() > tape) {
                        positionedCorrectly = true;
                        telemetry.addData("arrived", 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // if over, go backward with 0.1
            try {
                if (seenTape == true && lineSensor.getLightDetected() < tape && stuff.isTapeColorWhite()) {
                    while (lineSensor.getLightDetected() < tape) {
                        robot.rightMotor.setPower(-0.1);
                        robot.leftMotor.setPower(-0.1);
                        telemetry.addData("back", 1);
                    }
                    positionedCorrectly = true;
                    telemetry.addData("arrived", 1);
                }
                if (seenTape == true && lineSensor.getLightDetected() > tape && stuff.isTapeColorWhite() == false) {
                    while (lineSensor.getLightDetected() < tape) {
                        robot.rightMotor.setPower(-0.1);
                        robot.leftMotor.setPower(-0.1);
                        telemetry.addData("back", 1);
                    }
                    positionedCorrectly = true;
                    telemetry.addData("arrived", 1);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            telemetry.update();

            // stop on the line
            if (positionedCorrectly == true) {
                robot.leftMotor.setPower(0);
                robot.rightMotor.setPower(0);
                //open claw
                robot.leftClaw.setPosition(Servo.MAX_POSITION);


                // pick up ball
                robot.rightClaw.setPosition(0.68);
                sleep(1000); // assumption; needs testing and change
                robot.leftClaw.setPosition(0.2);
                sleep(1000);
                robot.rightClaw.setPosition(Servo.MIN_POSITION);
                sleep(1000);
                break;
            }
        }
    }
}
