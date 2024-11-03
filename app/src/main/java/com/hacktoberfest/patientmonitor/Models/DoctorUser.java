package com.hacktoberfest.patientmonitor.Models;

public class DoctorUser extends User {
    private String specialization;
    private int experience;
    private String qualification;
    private String hospitalName;

    public DoctorUser() {
        super();
    }

    public DoctorUser(String name, String email, String password, String role, String specialization, int experience, String qualification, String hospitalName) {
        super(name, email, password, role);
        this.specialization = specialization;
        this.experience = experience;
        this.qualification = qualification;
        this.hospitalName = hospitalName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
}
