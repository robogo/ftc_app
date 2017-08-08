package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;


@Autonomous(name = "turn test", group = "Aidan")
public class RobotTurningTest extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    ColorSensor sensorRGB;
    double threshold;
    ElapsedTime time = new ElapsedTime();

    double power = 1;   // <<<<==============      Adjust power here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Override
    public void runOpMode() {
        DcMotor leftMotor = null;
        DcMotor rightMotor = null;
        sensorRGB = hardwareMap.colorSensor.get("sensor_color");
        sensorRGB.enableLed(true);
        leftMotor = hardwareMap.dcMotor.get("left_drive");
        rightMotor = hardwareMap.dcMotor.get("right_drive");
        threshold = sensorRGB.argb();
        waitForStart();
        time.reset();
        sleep(400);
        while (opModeIsActive()) {
            leftMotor.setPower(power);
            rightMotor.setPower(power);
            telemetry.addData("threshold:" , threshold);
            telemetry.addData("color", sensorRGB.argb());
            if (sensorRGB.argb()==threshold) {
                leftMotor.setPower(0);
                rightMotor.setPower(0);
                telemetry.addData("Turn speed: 360 degrees/", time.seconds());
                telemetry.update();
            }
        }
    }
}