package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.robogo.SensorMock;

import static java.lang.Thread.*;

@Autonomous(name = "DEBUG", group = "Aidan")
@Disabled
public class DebugOpMode extends OpMode {

    HardwarePushbot robot = new HardwarePushbot();

    OpticalDistanceSensor ods;
    double total;
    double count;

    @Override
    public void init() {
        ods = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        ods.enableLed(true);
        ((SensorMock) ods).setTimeValues(new double[]{1.0}, new double[]{0.05});
    }

    @Override
    public void start() {
        robot.init(hardwareMap);
        robot.leftMotor.setPower(0.5);
        robot.rightMotor.setPower(0.5);
    }

    long time = System.nanoTime();

    @Override
    public void loop() {

        total += ods.getLightDetected();
        count++;
        if (((System.nanoTime() - time) / 1000000000.0) > 1.0) {
            telemetry.addData("average ods value", total / count);
            time = System.nanoTime();
        }
    }
}
