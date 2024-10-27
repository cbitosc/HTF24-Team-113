package com.hacktoberfest.patientmonitor.Models;
public class Doctor {
    String id;
    private String name;
    private String specialization;
    // Optional: add an image resource or URI for profile picture

    public Doctor(String id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }
}
