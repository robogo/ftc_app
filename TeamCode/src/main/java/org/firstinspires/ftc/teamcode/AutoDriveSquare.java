package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by aidan on 5/24/2017.
 */
@Autonomous(name = "porportionalLinefollower", group = "Aidan")
public class AutoDriveSquare extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor lineSensor;

    final static double CONSTANT_SPEED = 0.3;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        lineSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods"); // names of the stuff we need to set in the config.
        lineSensor.enableLed(true);
        double leftPower;
        double rightPower;
        this.waitForStart();
        while (opModeIsActive()) {
            double a = lineSensor.getLightDetected();
            double correction = 0.2 - a;
            if (correction <= 0.1) {
                leftPower = CONSTANT_SPEED - correction;
                rightPower = CONSTANT_SPEED;
            } else {
                leftPower = CONSTANT_SPEED + correction;
                rightPower = CONSTANT_SPEED;
            }
            robot.leftMotor.setPower(leftPower);
            robot.rightMotor.setPower(rightPower);
        }
    }

}

