package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import static java.lang.Thread.*;

/**
 * Created by aidan on 5/17/2017.
 */

public class opmode2 extends OpMode{

    HardwarePushbot robot = new HardwarePushbot();
    DcMotor leftMotor = null;
    DcMotor rightMotor = null;


    @Override
    public void init() {
        robot.init(hardwareMap);


        leftMotor = hardwareMap.dcMotor.get("left_drive");
        rightMotor = hardwareMap.dcMotor.get("right_drive");

        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void start() {
        for(int count = 0; count<4; count++){
            robot.leftMotor.setPower(0.5);
            robot.rightMotor.setPower(0);
            try {
                sleep(1000);
            } catch(InterruptedException ex) {
                currentThread().interrupt();
            }
            robot.rightMotor.setPower(0.5);
            robot.leftMotor.setPower(0);
            try {
                sleep(1000);
            } catch(InterruptedException ex) {
                currentThread().interrupt();
            }
        }

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