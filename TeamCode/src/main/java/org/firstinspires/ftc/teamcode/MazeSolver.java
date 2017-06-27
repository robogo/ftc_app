package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by Dan on 6/24/2017.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "MazeSolver", group = "Ryan")
public class MazeSolver extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor lightSensor;

    double PerfectColorValue;
    static final double LineFollowingSpeed = 0.2;
    double leftPower;
    double rightPower;

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);
        //set direction of the motors
        robot.leftMotor.setDirection(DcMotor.Direction.REVERSE);
        robot.rightMotor.setDirection(DcMotor.Direction.FORWARD);
        //define ODS
        lightSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods");
        //enable ODS
        lightSensor.enableLed(true);
        //set perfectColorValue to whatever the ODS sees
        PerfectColorValue = lightSensor.getLightDetected();
        this.waitForStart();
        while (opModeIsActive() && !isStopRequested()) {
            double correction = (PerfectColorValue - lightSensor.getLightDetected());
            //reads white, turns right
            if (correction <= 0){
                leftPower = LineFollowingSpeed - correction;
                rightPower = LineFollowingSpeed;
            }
            //reads black, turns left
            else {
                leftPower = LineFollowingSpeed;
                rightPower = LineFollowingSpeed + correction;
            }

            robot.leftMotor.setPower(leftPower);
            robot.rightMotor.setPower(rightPower);
            telemetry.addData("Raw",    lightSensor.getRawLightDetected());
            telemetry.addData("Normal", lightSensor.getLightDetected());
            telemetry.update();

        }
    }
}

