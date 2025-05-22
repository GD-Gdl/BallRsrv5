package com.example.ballrsrv;

public class Booking {
    private String courtName;
    private String date;
    private String time;
    private String status;
    private String userEmail;

    // Empty constructor required for Firebase
    public Booking() {
    }

    public Booking(String courtName, String date, String time, String status, String userEmail) {
        this.courtName = courtName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.userEmail = userEmail;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
