package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.AppUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by xinchen on 6/24/2017.
 */

public class EnvLight {
    double[] numbers = null;
    double threshold;

    public EnvLight read() throws IOException {
        File data = new File(AppUtil.FIRST_FOLDER, "ods.data");
        String content = ReadWriteFile.readFileOrThrow(data);
        String[] values = content.split(",");
        EnvLight light = new EnvLight();
        light.numbers = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            light.numbers[i] = Double.parseDouble(values[i]);
        }
        light.threshold = numbers[2];
        return light;
    }

    private boolean isTapeColorWhite() {

        if (numbers[1] < numbers[0]) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isGround(double value) {

        if (value < threshold && isTapeColorWhite() == true) {
            return true;
        } else if (value > threshold && isTapeColorWhite() == false) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLine(double value) {

        if (value > threshold && isTapeColorWhite() == true) {
            return true;
        } else if (value < threshold && isTapeColorWhite() == false) {
            return true;
        } else {
            return false;
        }
    }
}
