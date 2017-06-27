package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Dan on 6/25/2017.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "LightCalibrationOpmode", group = "Ryan")
public class LightCalibrationOpmode extends LinearOpMode {

    public double lineColor;
    public double groundColor;
    double speed = 0.5;
    HardwarePushbot robot = new HardwarePushbot();


    OpticalDistanceSensor lightSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        robot.leftMotor.setDirection(DcMotor.Direction.REVERSE);
        robot.rightMotor.setDirection(DcMotor.Direction.FORWARD);
        lightSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        lightSensor.enableLed(true);
        lineColor = lightSensor.getLightDetected();
        this.waitForStart();

        robot.leftMotor.setPower(speed);
        robot.rightMotor.setPower(speed);
        sleep(500);
        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);
        groundColor = lightSensor.getLightDetected();

        EnvLight envColors = new EnvLight(lineColor, groundColor);
        envColors.telemetry = telemetry;

        envColors.writeFile();
        envColors.readFile();

        sleep(5000);
    }




}
