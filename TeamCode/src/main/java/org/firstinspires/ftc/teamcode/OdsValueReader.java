package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.AppUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by xinchen on 6/24/2017.
 */

public class OdsValueReader {
    public static double[] read() throws IOException {
        File data = new File(AppUtil.FIRST_FOLDER, "ods.data");
        String content = ReadWriteFile.readFileOrThrow(data);
        String[] values = content.split(",");
        double[] numbers = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            numbers[i] = Double.parseDouble(values[i]);
        }
        return numbers;
    }
}
