package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by Ryan on 5/14/2017.
 */

@TeleOp(name="Opmode2", group="Ryan")  // @Autonomous(...) is the other common choice

public class Opmode2 extends OpMode {

    HardwarePushbot robot = new HardwarePushbot();
    //DcMotor leftMotor = null;
    //DcMotor rightMotor = null;

    Servo leftClaw = null;

    int count;

    @Override
    public void init() {
        robot.init(hardwareMap);


        //leftMotor = hardwareMap.dcMotor.get("left_drive");
        //rightMotor = hardwareMap.dcMotor.get("right_drive");

        leftClaw = hardwareMap.servo.get("left_claw");

        //leftMotor.setDirection(DcMotor.Direction.FORWARD);
        //rightMotor.setDirection(DcMotor.Direction.REVERSE);
    }


    @Override
    public void loop() {
        //go forward
        if (gamepad1.right_stick_y == 1) {


            robot.leftMotor.setPower(1);
            robot.rightMotor.setPower(1);
        }else  {
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
        }
        //go backward
        if (gamepad1.right_stick_y == -1) {

            robot.rightMotor.setPower(-1);
            robot.leftMotor.setPower(-1);

        }else {
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
        }
        //turn left
        if (gamepad1.left_stick_x == -1) {

            robot.rightMotor.setPower(1);
            robot.leftMotor.setPower(-1);

        }else {
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
        }
        //turn right
        if (gamepad1.left_stick_x == 1) {

            robot.rightMotor.setPower(-1);
            robot.leftMotor.setPower(1);

        }else {
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
        }
        //servo movement
        if (gamepad1.a == true) {

            robot.leftClaw.setPosition(0.8);

        }else {
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
        }

    }
}
