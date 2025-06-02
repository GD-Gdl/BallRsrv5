package com.example.ballrsrv;

public class BookingRequest {
    private String id;
    private String userId;
    private String userName;
    private String email;
    private String courtId;
    private String date;
    private String time;
    private String timeSlot;
    private int duration;
    private String status;
    private double price;
    private double totalPrice;
    private String bookingDetails;
    private String paymentStatus;
    private String paymentMethod;
    private String referenceCode;

    public BookingRequest() {
        // Default constructor required for Firebase
        this.paymentStatus = "pending";
        this.paymentMethod = "none";
        this.referenceCode = "";
    }

    public BookingRequest(String id, String userId, String userName, String email, String courtId,
                         String date, String timeSlot, String status, double price) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.courtId = courtId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
        this.price = price;
        this.paymentStatus = "pending";
        this.paymentMethod = "none";
        this.referenceCode = "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getBookingDetails() { return bookingDetails; }
    public void setBookingDetails(String bookingDetails) { this.bookingDetails = bookingDetails; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
}
