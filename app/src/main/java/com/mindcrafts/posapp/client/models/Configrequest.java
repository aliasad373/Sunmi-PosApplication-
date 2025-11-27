package com.mindcrafts.posapp.client.models;

public class Configrequest {
    private String serial_number;

    // Constructor
    public Configrequest(String serial_number) {
        this.serial_number = serial_number;
    }

    // Getter
    public String getSerial_number() {
        return serial_number;
    }

    // Setter
    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    @Override
    public String toString() {
        return "Device{" +
                "serial_number='" + serial_number + '\'' +
                '}';
    }
}

