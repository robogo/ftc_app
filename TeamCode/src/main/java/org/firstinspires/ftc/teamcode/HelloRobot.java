package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="HelloRobot", group="Test")
public class HelloRobot extends OpMode {
    @Override
    public void init() {
    }

    @Override
    public void loop() {
        telemetry.addLine("Hello Robot");
        telemetry.addData("My name", "Bob");
        telemetry.addData("W", 1234.5f);
        telemetry.update();
    }
}
