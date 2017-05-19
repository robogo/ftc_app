package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Created by Aidan on 5/17/2017.
 */

public class opmode1 extends OpMode{

    HardwarePushbot robot = new HardwarePushbot();
    DcMotor leftMotor = null;
    DcMotor rightMotor = null;

    int count;

    @Override
    public void init() {
        robot.init(hardwareMap);


        leftMotor = hardwareMap.dcMotor.get("left_drive");
        rightMotor = hardwareMap.dcMotor.get("right_drive");

        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
    }


    @Override
    public void loop() {
        if (gamepad1.a == true) {

            robot.leftMotor.setPower(0.5);
            robot.rightMotor.setPower(0);
        }else  {
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
        }
        if (gamepad1.b == true) {

            robot.rightMotor.setPower(0.5);
            robot.leftMotor.setPower(0);

        }else {
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
        }
    }
}

