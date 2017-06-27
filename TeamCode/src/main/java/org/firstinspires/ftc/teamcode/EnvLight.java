package org.firstinspires.ftc.teamcode;


import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.qualcomm.robotcore.robocol.TelemetryMessage;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.TelemetryImpl;
import org.firstinspires.ftc.robotcore.internal.TelemetryInternal;

/**
 * Created by Dan on 6/25/2017.
 */

public class EnvLight {
    public double lineColor;
    public double groundColor;
    public double threshold;
    public double anArray[] = new double[3];
    public Telemetry telemetry;


    public EnvLight() {

    }

    public EnvLight(double line, double ground) {
        this.lineColor = line;
        this.groundColor = ground;
        this.threshold = (this.lineColor + this.groundColor) / 2;

    }



    boolean isGround(double value) {
        //ground is lighter than line so black line on white ground
        if (groundColor >= lineColor) {
            if (value > this.threshold) {
                return true;
            } else {
                return false;
            }
            //white line on black
        } else {
            if (value <= this.threshold) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void writeFile() {
        try {
             File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES);

            PrintWriter writer = new PrintWriter(path + "/env_light.txt", "UTF-8");

            writer.println(this.lineColor);
            writer.println(this.groundColor);

            writer.println(this.threshold);
            if (this.telemetry != null) {
                telemetry.addData("lineColor", this.lineColor);
                telemetry.addData("groundColor", this.groundColor);
                telemetry.addData("threshold", this.threshold);
                telemetry.update();
            }

            writer.close();
        } catch (IOException e) {
            if (this.telemetry != null) {
                telemetry.addData("exception msg:",  e.getMessage());

            }
        }
    }

    public void readFile() {
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES);
            BufferedReader in = new BufferedReader(new FileReader(path +"/env_light.txt"));
            String str;
            int i = 0;
            while ((str = in.readLine()) != null) {
                anArray[i] = Double.parseDouble(str);
                i++;
            }
            in.close();

            this.lineColor = anArray[0];
            this.groundColor = anArray[1];
            this.threshold = anArray[2];

            if (this.telemetry != null) {
                telemetry.addData("lineColor",  anArray[0]);
                telemetry.addData("groundColor",  anArray[1]);
                telemetry.addData("threshold",  anArray[2]);
                telemetry.update();
            }

        } catch (IOException e) {
            if (this.telemetry != null) {
                telemetry.addData("exception msg:",  e.getMessage());
                telemetry.update();

            }
        }
    }
}
