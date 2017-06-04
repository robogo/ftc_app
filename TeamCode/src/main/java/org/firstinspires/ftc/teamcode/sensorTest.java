package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by aidan on 5/24/2017.
 */
@TeleOp(name = "sensorTest", group = "Aidan")
public class sensorTest extends OpMode {
    HardwarePushbot robot = new HardwarePushbot();
    OpticalDistanceSensor lineSensor; // Methods inherited from interface com.qualcomm.robotcore.hardware.LightSensor
    IrSeekerSensor seekerSensor;
    TouchSensor touchSensor;


    @Override
    public void init() {

        robot.init(hardwareMap);
        lineSensor = hardwareMap.opticalDistanceSensor.get("sensor_ods"); // names of the stuff we need to set in the config.
        lineSensor.enableLed(true);
        seekerSensor = hardwareMap.irSeekerSensor.get("sensor_seeker");
        touchSensor = hardwareMap.touchSensor.get("sensor_touch");

    }

    @Override
    public void loop() {

        double b = lineSensor.getLightDetected(); // Get the amount of light detected by the sensor, scaled from 0-1
        // Note that returned values INCREASE as the light energy INCREASES.

        double a = lineSensor.getRawLightDetected(); // Returns a signal whose strength is proportional to the intensity of the light measured.
        // Note that returned values INCREASE as the light energy INCREASES.
        // The units in which this signal is returned are unspecified

        double c = seekerSensor.getAngle(); // Estimated angle in which the signal is coming from

        double d = seekerSensor.getStrength(); // IR Signal strength

        double e = touchSensor.getValue(); // Represents how much force is applied to the touchSensor sensor; for some touchSensor sensors this value will only ever be 0 or 1.

        boolean f = touchSensor.isPressed(); // if its pressed

        telemetry.addData("ODS_raw", a);
        telemetry.addData("ODS", b);
        telemetry.addData("Angle", c);
        telemetry.addData("sig_strength", d);
        telemetry.addData("touch_force", e);
        telemetry.addData("pressed?", f);
        telemetry.update();
//robot.rightMotor.setPower(a);
    }
}

/*WHITE

sensor: 0.4
sensor RAW: 2
BLACK
sensor: 0.02
sensor RAW: 0.1
*/
