package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by Dan on 6/17/2017.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "BallPickup", group = "Ryan")
public class BallPickup extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor lightSensor;

    double openPosition = 0.7;
    double closedPosition = 0.20;
    double downPosition = 0.90;
    double upPosition = 0.20;
    double PerfectColorValue;
    static final double LineFollowingSpeed = 0.2;

    //before initializing, we must put the robot so the ODS sees the white line
    @Override
    public void runOpMode() throws InterruptedException {

        //initialize robot
        robot.init(hardwareMap);

        //close the claw
        robot.leftClaw.setPosition(closedPosition);

        //lift the arm
        robot.rightClaw.setPosition(upPosition);

        //set motor directions
        robot.leftMotor.setDirection(DcMotor.Direction.REVERSE);
        robot.rightMotor.setDirection(DcMotor.Direction.FORWARD);
        //define ODS
        lightSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        //enable ODS
        lightSensor.enableLed(true);
        //set perfectColorValue to whatever the ODS sees
        PerfectColorValue = lightSensor.getLightDetected();

        this.waitForStart();

        //set motor power
        robot.leftMotor.setPower(LineFollowingSpeed);
        robot.rightMotor.setPower(LineFollowingSpeed);
        //wait until ODS sees white
        while (opModeIsActive() && lightSensor.getLightDetected() >= PerfectColorValue) {

            telemetry.addData("Normal", lightSensor.getLightDetected());
            telemetry.addData("PerfectColorValue", PerfectColorValue);
            telemetry.update();
        }

        //stop the robot
        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);
        //open the claw

        robot.leftClaw.setPosition(openPosition);
        sleep(1000);

        //lower the arm

        robot.rightClaw.setPosition(downPosition);
        sleep(1000);
        //close the claw

        robot.leftClaw.setPosition(closedPosition);
        sleep(1000);

        //lift the arm

        robot.rightClaw.setPosition(upPosition);
        sleep(1000);


    }
}
