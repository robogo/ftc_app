package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ReadWriteFile;
import com.robogo.Mock;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.firstinspires.ftc.robotcore.internal.AppUtil;

/**
 * Created by xinchen on 6/24/2017.
 */
@Autonomous(name = "set ground and tape values", group = "Aidan")
public class LightCalibrationOpMode extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor ods;
    TouchSensor touch;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        ods = hardwareMap.opticalDistanceSensor.get("sensor_ods"); // names of the stuff we need to set in the config.
        ods.enableLed(true);
        touch = hardwareMap.touchSensor.get("sensor_touch");
        ((Mock)touch).setData(new double[] { 5, 5.5, 8, 8.5 }, new double[] { 1.0, 0.0, 1.0, 0.0 });
        telemetry.addLine("ground, tape, then perfect value. press touch button when sensor is on next value");
        telemetry.update();
        double ground = ods.getLightDetected();
        while (!touch.isPressed()) {
            this.sleep(100);
        }
        double tape = ods.getLightDetected();
        while (!touch.isPressed()) {
            this.sleep(100);
        }
        double perfectValue = ods.getLightDetected();
        String values = String.format("%f,%f,%f", ground, tape, perfectValue);
        ReadWriteFile.writeFile(AppUtil.FIRST_FOLDER, "ods.data", values);
    }
}