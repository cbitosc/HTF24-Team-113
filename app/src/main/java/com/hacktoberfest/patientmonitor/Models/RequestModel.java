package com.hacktoberfest.patientmonitor.Models;

public class RequestModel {
    private String patientId;
    private String patientName;
    private String status;
    private long timestamp;

    public RequestModel() {
    }

    public RequestModel(String patientId, String patientName, String status, long timestamp) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
