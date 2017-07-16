package com.robogo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmulatedHardwareFactory extends HardwareFactory{
    private EmulatedHardwareMap map;
    private Context       context;
    private Activity      activity;
    private TextView      text;

    public EmulatedHardwareFactory(Context context, Activity activity, TextView text) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.text = text;
    }

    @Override
    public HardwareMap createHardwareMap(EventLoopManager manager) throws RobotCoreException, InterruptedException {
        if (map == null) {
            RobotLog.i("EmulatedHardwareFactory.createHardwareMap()");
            map = new EmulatedHardwareMap(context);
            map.dcMotorController.put("motor_controller", new MockDcMotorController());
            map.dcMotor.put("left_drive", new MockDcMotor(this));
            map.dcMotor.put("right_drive", new MockDcMotor(this));
            map.dcMotor.put("left_arm", new MockDcMotor(this));
            map.dcMotor.put("right_arm", new MockDcMotor(this));
            map.servo.put("left_hand", new MockServo(this));
            map.servo.put("right_hand", new MockServo(this));
            map.lightSensor.put("sensor_light", new MockLightSensor(this));
            map.touchSensor.put("sensor_touch", new MockTouchSensor(this));
            map.opticalDistanceSensor.put("sensor_ods", new MockOpticalDistanceSensor(this));
        }

        return map;
    }

    public void deviceStateChanged() {
        final StringBuilder sb = new StringBuilder(512);
        for (Map.Entry<String, List<HardwareDevice>> pair : this.map.entrySet()) {
            for (HardwareDevice device : pair.getValue()) {
                if (device instanceof Mock) {
                    sb.append(pair.getKey())
                        .append(":")
                        .append(((Mock)device).getData())
                        .append("\n");
                }
            }
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(sb.toString());
                text.setVisibility(View.VISIBLE);
            }
        });
    }

    class EmulatedHardwareMap extends HardwareMap {
        public EmulatedHardwareMap(Context context) {
            super(context);
        }

        public Set<Map.Entry<String, List<HardwareDevice>>> entrySet() {
            return this.allDevicesMap.entrySet();
        }
    }
}
