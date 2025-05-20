package com.example.ballrsrv;

public class Booking {
    private String id;
    private String userId;
    private String userName;
    private String date;
    private String time;
    private int duration;
    private double totalPrice;
    private String status;
    private String paymentStatus;
    private String paymentMethod;

    // Required for Firebase
    public Booking() {}

    public Booking(String id, String userId, String userName, String date, String time, 
                  int duration, double totalPrice, String status, String paymentStatus, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
