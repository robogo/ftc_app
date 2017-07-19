package com.robogo;

public class SensorMock extends Mock {

    private long start;
    private DataSource source;

    protected SensorMock(EmulatedHardwareFactory factory) {
        super(factory);
    }

    public void setTimeValues(double[] seconds, double[] values) {
        this.source = new TimeValueDataSource(seconds, values);
        this.start = System.nanoTime();
    }

    public void setDataSource(DataSource source) {
        this.source = source;
        this.start = System.nanoTime();
    }

    @Override
    public double getData() {
        if (source == null) {
            return 0.0;
        }

        double seconds = (double) (System.nanoTime() - start) / 1000000000.0;
        double value = source.getData(seconds);
        this.setData(value);
        return value;
    }

    static class TimeValueDataSource implements DataSource {
        private double[] seconds;
        private double[] values;

        public TimeValueDataSource(double[] seconds, double[] values) {
            this.seconds = seconds;
            this.values = values;
        }

        @Override
        public double getData(double time) {
            double value = 0.0;
            for (int i = 0; i < seconds.length; i++) {
                if (seconds[i] > time) {
                    break;
                }
                value = values[i];
            }
            return value;
        }
    }
}
