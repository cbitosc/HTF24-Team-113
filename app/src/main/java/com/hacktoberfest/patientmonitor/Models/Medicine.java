package com.hacktoberfest.patientmonitor.Models;
public class Medicine {
    private String name;
    private String time;
    private String instruction;

    public Medicine(String name, String time, String instruction) {
        this.name = name;
        this.time = time;
        this.instruction = instruction;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getInstruction() {
        return instruction;
    }
}