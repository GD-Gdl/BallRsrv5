package com.example.ballrsrv;

public class Booking {
    private String courtName;
    private String date;
    private String time;
    private String status;
    private String userEmail;
    private int duration;
    private double totalPrice;
    private String paymentStatus;
    private String paymentMethod;

    // Empty constructor required for Firebase
    public Booking() {
    }

    public Booking(String courtName, String date, String time, String status, String userEmail,
                  int duration, double totalPrice, String paymentStatus, String paymentMethod) {
        this.courtName = courtName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.userEmail = userEmail;
        this.duration = duration;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
