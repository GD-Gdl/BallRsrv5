package com.example.ballrsrv;

public class BookingRequest {
    private String id;
    private String userName;
    private String bookingDetails;
    private String status; // "pending", "accepted", "denied"
    private String date;
    private String timeSlot;

    // Required empty constructor for Firebase
    public BookingRequest() {
    }

    public BookingRequest(String id, String userName, String bookingDetails, String status, String date, String timeSlot) {
        this.id = id;
        this.userName = userName;
        this.bookingDetails = bookingDetails;
        this.status = status;
        this.date = date;
        this.timeSlot = timeSlot;
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
}
