package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;


@Autonomous(name = "speed test", group = "Aidan")
public class RobotSpeedTest extends LinearOpMode {
    //HardwarePushbot robot = new HardwarePushbot();
    ColorSensor sensorRGB;
    //EnvLight el = new EnvLight();
    ElapsedTime time = new ElapsedTime();
    //TouchSensor touch;
    double threshold;

    @Override
    public void runOpMode() {
        DcMotor leftMotor = null;
        DcMotor rightMotor = null;
        sensorRGB = hardwareMap.colorSensor.get("sensor_color");
        sensorRGB.enableLed(true);
        //touch = hardwareMap.touchSensor.get("sensor_touch");
        leftMotor = hardwareMap.dcMotor.get("left_drive");
        rightMotor = hardwareMap.dcMotor.get("right_drive");
        threshold = sensorRGB.argb();
        double power = 0.9;
       /* while (!isStarted()){
            if (touch.isPressed()){
                power += 0.1;
            }
        }*/
        waitForStart();
        leftMotor.setPower(-power);
        rightMotor.setPower(power);
        time.reset();
        sleep(400);
        while (opModeIsActive()) {
            telemetry.addData("threshold:" , threshold);
            telemetry.addData("color", sensorRGB.argb());
            telemetry.addData("power", power);
            telemetry.update();
            if (sensorRGB.argb()==threshold) {  // line white or black switch > or <
                leftMotor.setPower(0);
                rightMotor.setPower(0);
                telemetry.addData("M/S = " , 0.58/time.seconds());
                telemetry.update();
                break;
            }
        }
        while (!isStopRequested()){
            idle();
        }
    }
}