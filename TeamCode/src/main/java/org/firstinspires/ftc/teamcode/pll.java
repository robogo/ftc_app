package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by xin on 6/11/2017.
 */
// this assumes the ground is dark and the tape is light
// also the robot will be placed on the right side of the line
// after you press init the sensor has to be sensing the ground. then you have 5 seconds to move it so it only senses the tape
// once both the tape and ground variables appear in telemetry then you can set it on the side of the line and press start
public class pll extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor ods;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        ods.enableLed(true);
        double ground = ods.getLightDetected();
        wait(5000);
        double tape = ods.getLightDetected();
        double perfectValue = ground + tape / 2;
        telemetry.addData("ground", ground);
        telemetry.addData("tape", tape);
        telemetry.update();
        double correction;
        double driveSpeed = 0.3;
        double leftPower;
        double rightPower;
        waitForStart();

        while (opModeIsActive()) {
            correction = perfectValue - ods.getLightDetected();
            if (correction < perfectValue) {
                leftPower = (driveSpeed - correction);
                rightPower = (driveSpeed);
            } else {
                leftPower = driveSpeed;
                rightPower = driveSpeed + correction;
            }
            telemetry.addData("light level", ods.getLightDetected());
            robot.leftMotor.setPower(leftPower);
            robot.rightMotor.setPower(rightPower);
        }

    }
}
