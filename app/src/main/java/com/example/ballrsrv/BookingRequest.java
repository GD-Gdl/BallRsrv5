package com.example.ballrsrv;

public class BookingRequest {
    private String id;
    private String userName;
    private String email;
    private String bookingDetails;
    private String status; // "pending", "accepted", "denied"
    private String date;
    private String timeSlot;
    private int duration;
    private double totalPrice;
    private String paymentStatus;
    private String paymentMethod;

    // Required empty constructor for Firebase
    public BookingRequest() {
    }

    public BookingRequest(String id, String userName, String email, String bookingDetails, String status, String date, String timeSlot) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.bookingDetails = bookingDetails;
        this.status = status;
        this.date = date;
        this.timeSlot = timeSlot;
        this.paymentStatus = "pending";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(String bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
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
