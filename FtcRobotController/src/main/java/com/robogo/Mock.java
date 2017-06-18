package com.robogo;

public class Mock {
    private long start;
    private double[] time;
    private double[] value;

    public void setData(double[] time, double[] value) {
        this.time = time;
        this.value = value;
        this.start = System.nanoTime();
    }

    protected double getData() {
        if (time == null) {
            return 0.0;
        }
        double seconds = (double)(System.nanoTime() - start) / 1000000000.0;
        double data = 0.0;
        for (int i = 0; i < time.length; i++) {
            if (time[i] > seconds) {
                break;
            }
            data = value[i];
        }
        return data;
    }
}
