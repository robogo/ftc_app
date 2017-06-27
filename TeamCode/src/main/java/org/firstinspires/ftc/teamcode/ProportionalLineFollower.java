package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by Dan on 6/4/2017.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "ProportionalLineFollower", group = "Ryan")
public class ProportionalLineFollower extends LinearOpMode {

    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor lightSensor;

    double PerfectColorValue;
    static final double LineFollowingSpeed = .075d;
    double leftPower;
    double rightPower;
    char Turns[] = new char[100];
    int intersectionAmount = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        lightSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        lightSensor.enableLed(true);
        PerfectColorValue = lightSensor.getLightDetected();

        this.waitForStart();

        robot.leftMotor.setPower(LineFollowingSpeed);
        robot.rightMotor.setPower(LineFollowingSpeed);

        while (opModeIsActive() && lightSensor.getLightDetected() < PerfectColorValue) {
            telemetry.addData("Raw", lightSensor.getRawLightDetected());
            telemetry.addData("Normal", lightSensor.getLightDetected());
            telemetry.update();
        }


        while (lightSensor.getLightDetected() > PerfectColorValue && !isStopRequested()) {
            robot.rightMotor.setPower(0);
            robot.leftMotor.setPower(0.075);
        }

        while (opModeIsActive() && !isStopRequested()) {
            double correction = (PerfectColorValue - lightSensor.getLightDetected());
            //reads white, turns right
            if (correction <= 0) {
                if (correction <= -1) {
                    Turns[intersectionAmount] = 'L';
                    leftPower = LineFollowingSpeed;
                    rightPower = -LineFollowingSpeed;
                    intersectionAmount++;

                } else {
                    leftPower = LineFollowingSpeed - correction;
                    rightPower = LineFollowingSpeed;
                }
            }

            //reads black, turns left
            if (correction >= 0) {
                if (correction >= 1) {
                    Turns[intersectionAmount] = 'R';
                    leftPower = -LineFollowingSpeed;
                    rightPower = LineFollowingSpeed;
                    intersectionAmount++;
                } else {
                    leftPower = LineFollowingSpeed;
                    rightPower = LineFollowingSpeed + correction;
                }

                robot.leftMotor.setPower(leftPower);
                robot.rightMotor.setPower(rightPower);
                telemetry.addData("Raw", lightSensor.getRawLightDetected());
                telemetry.addData("Normal", lightSensor.getLightDetected());
                telemetry.update();

            }
            robot.leftMotor.setPower(0.0);
            robot.rightMotor.setPower(0.0);
        }
    }
}
