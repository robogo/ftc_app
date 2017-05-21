package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

/**
 * Created by aidan on 5/17/2017.
 */

public class RcOpMode extends OpMode {

    HardwarePushbot robot = new HardwarePushbot();
    float left;
    float right;

    @Override
    public void init() {
        robot.init(hardwareMap);
    }

    @Override
    public void loop() {
        left = -gamepad1.left_stick_y - gamepad1.right_stick_x;
        right = -gamepad1.left_stick_y + gamepad1.right_stick_x;
        double max = Math.max(Math.abs(left), Math.abs(right));
        if (max > 1) {
            left /= max;
            right /= max;
        }
        robot.leftMotor.setPower(left);
        robot.rightMotor.setPower(right);
    }
}