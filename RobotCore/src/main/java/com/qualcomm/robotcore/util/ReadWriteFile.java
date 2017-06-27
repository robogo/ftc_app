package com.qualcomm.robotcore.util;

import org.firstinspires.ftc.robotcore.internal.AppUtil;
import org.firstinspires.ftc.robotcore.internal.network.RobotCoreCommandList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("WeakerAccess")
public class ReadWriteFile {

    public static String readFileOrThrow(File file) throws IOException {
        FileReader reader = new FileReader(file);
        StringBuilder content = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }

    public static String readFile(File file) {
        try {
          return readFileOrThrow(file);
        } catch (IOException e) {
            RobotLog.e("Error reading file: " + e.getMessage());
        }
        return "";
    }

    //----------------------------------------------------------------------------------------------

    public static byte[] readBytes(RobotCoreCommandList.FWImage fwImage) {
        if (fwImage.isAsset) {
            return readAssetBytes(fwImage.file);
        } else {
            return readFileBytes(fwImage.file);
        }
    }

    public static byte[] readAssetBytes(File assetFile) {
        try {
            return readAssetBytesOrThrow(assetFile);
        } catch (IOException e) {
            RobotLog.e("Error reading asset: " + e.getMessage());
        }
        return new byte[0];
    }

    public static byte[] readFileBytes(File file) {
        try {
            return readFileBytesOrThrow(file);
        } catch (IOException e) {
            RobotLog.e("Error reading file: " + e.getMessage());
        }
        return new byte[0];
    }

    public static byte[] readAssetBytesOrThrow(File assetFile) throws IOException {
        InputStream inputStream = AppUtil.getDefContext().getAssets().open(assetFile.getPath());
        return readBytesOrThrow(0, inputStream);
    }

    public static byte[] readFileBytesOrThrow(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        return readBytesOrThrow((int)file.length(), inputStream);
    }

    protected static byte[] readBytesOrThrow(int cbSizeHint, InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(cbSizeHint);
        byte[] buffer = new byte[1000];     // size is arbitrary
        try {
            for (;;) {
                int cbRead = inputStream.read(buffer);
                if (cbRead == -1) {
                    break;  // end of stream hit
                }
                byteArrayOutputStream.write(buffer);
            }
        } finally {
            inputStream.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    //----------------------------------------------------------------------------------------------

    public static void writeFile(File file, String fileContents) {
        writeFile(file.getParentFile(), file.getName(), fileContents);
    }

    public static void writeFile(File directory, String fileName, String fileContents) {
        try
        {
            AppUtil.getInstance().ensureDirs(directory);
            File file = new File(directory, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(fileContents);
            writer.flush();
            writer.close();
            AppUtil.getInstance().noteFileInMediaTransferProtocol(file);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}
