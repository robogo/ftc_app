package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.robogo.DataSource;
import com.robogo.SensorMock;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

@Autonomous(name = "HomeworkOpMode", group = "Homework")
@Disabled
public class HomeworkOpMode extends LinearOpMode {

    HardwarePushbot robot;
    OpticalDistanceSensor ods;

    @Override
    public void runOpMode() {

        robot = new HardwarePushbot();
        robot.init(hardwareMap);
        ods = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        ods.enableLed(true);
        ((SensorMock) ods).setDataSource(new DataSource() {
            @Override
            public double getData(double time) {
                return Math.sin(time * 1234);
            }
        });
        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();

        double[] values = collect(12);

        telemetry.addData("values", concat(values));
        telemetry.addData("min", find(values, 1));
        telemetry.addData("max", find(values, -1));
        telemetry.addData("average", sum(values) / values.length);
        telemetry.update();

        while (opModeIsActive()) {
            sleep(500);
            idle();
        }
    }

    private double[] collect(int count) {
        double[] values = new double[count];
        for (int i = 0; i < values.length; i++) {
            values[i] = ods.getLightDetected();
            sleep(400);

            telemetry.addData("Collected", i);
            telemetry.update();
        }
        return values;
    }

    private double sum(double[] values) {
        double sum = 0;
        for (double d : values) {
            sum += d;
        }
        return sum;
    }

    private double find(double[] values, int sign) {
        double value = 1;
        for (int i = 0; i < values.length; i++) {
            if (values[i] * sign < value) {
                value = values[i];
            }
        }
        if (value < 0) {
            value = Math.abs(value);
        }
        return value;
    }

    private String concat(double[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i])
                    .append((i + 1) % 3 == 0 ? "\n\t" : " ");
        }
        return sb.toString();
    }
}