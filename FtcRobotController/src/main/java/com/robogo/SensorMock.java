package com.robogo;

public class SensorMock extends Mock {

    private long start;
    private double[] time;
    private double[] values;

    protected SensorMock(EmulatedHardwareFactory factory) {
        super(factory);
    }

    public void setTimeValues(double[] time, double[] values) {
        this.time = time;
        this.values = values;
        this.start = System.nanoTime();
    }

    @Override
    public double getData() {
        double value = 0.0;
        if (time != null) {
            double seconds = (double) (System.nanoTime() - start) / 1000000000.0;
            for (int i = 0; i < time.length; i++) {
                if (time[i] > seconds) {
                    break;
                }
                value = values[i];
            }
        }

        this.setData(value);

        return value;
    }
}
