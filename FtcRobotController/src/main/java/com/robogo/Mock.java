package com.robogo;

public class Mock {

    private EmulatedHardwareFactory factory;
    private double data;

    protected Mock(EmulatedHardwareFactory factory){
        this.factory = factory;
    }

    public double getData() {
        return data;
    }

    protected void setData(double data) {
        if (this.data != data) {
            this.data = data;
            this.factory.deviceStateChanged();
        }
    }
}
