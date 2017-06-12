package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by xin on 6/11/2017.
 */
// this assumes the ground is dark and the tape is light
// also the robot will be placed on the right side of the line
// after you press init the sensor has to be sensing the ground. then you have to move it so it only senses the tape. Then press the touch sensor.
// once both the tape and ground variables appear in telemetry then you can set it on the side of the line and press start
@Autonomous(name = "ProportionalLineFollower", group = "Aidan")
public class pll extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor ods;
    TouchSensor touch;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        ods = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        ods.enableLed(true);
        touch = hardwareMap.touchSensor.get("sensor_touch");
        double ground = ods.getLightDetected();
        while (!touch.isPressed()) {
            this.sleep(100);
        }
        double tape = ods.getLightDetected();
       /* while (!touch.isPressed()) {
            this.sleep(100);
        } */
        double perfectValue = tape - 0.3 * (tape - ground);
        telemetry.addData("ground", ground);
        telemetry.addData("tape", tape);
        telemetry.addData("perfect value", perfectValue);
        telemetry.update();
        double correction;
        double driveSpeed = 0.1;
        double leftPower;
        double rightPower;
        waitForStart();

        while (opModeIsActive()) {
            //if (Math.abs(ods.getLightDetected() - ground) < 0.01) {
            //break;
            //}
            double light = ods.getLightDetected();
            if (light < ground) {
                light = ground;
            }
            correction = (light - perfectValue) / (tape - ground) * driveSpeed;
            if (correction < 0) {
                leftPower = (driveSpeed - correction);
                rightPower = (driveSpeed + correction);
            } else {
                leftPower = driveSpeed - correction;
                rightPower = driveSpeed + correction;
            }
            telemetry.addData("light level", light);
            telemetry.addData("correction", correction);
            telemetry.addData("leftpower", leftPower);
            telemetry.addData("rightpower",rightPower);
            telemetry.update();
            robot.leftMotor.setPower(leftPower);
            robot.rightMotor.setPower(rightPower);
        }
        //robot.leftMotor.setPower(0);
        // robot.rightMotor.setPower(0);
    }
}
