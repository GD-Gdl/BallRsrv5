package com.example.ballrsrv;

public class BookingRequest {
    private String id;
    private String userName;
    private String bookingDetails;
    private String status; // "pending", "accepted", "denied"

    public BookingRequest(String id, String userName, String bookingDetails, String status) {
        this.id = id;
        this.userName = userName;
        this.bookingDetails = bookingDetails;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getBookingDetails() {
        return bookingDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
