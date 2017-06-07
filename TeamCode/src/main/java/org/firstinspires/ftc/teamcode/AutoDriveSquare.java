package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by aidan on 5/24/2017.
 */

public class AutoDriveSquare extends LinearOpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor linesensor;
double constantSpeed = 0.5;
double perfectValue = 0.2;
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        while (true) {
            robot.leftMotor.setPower(constantSpeed);
            robot.rightMotor.setPower(constantSpeed);
            double correction = perfectValue - linesensor.getLightDetected();
if (correction <= 0){
    robot.leftMotor.setPower(constantSpeed - correction);
}
else{
    robot.rightMotor.setPower(constantSpeed + correction);
}
        }
        }

}

