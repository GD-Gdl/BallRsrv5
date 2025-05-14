package com.example.ballrsrv;

public class Booking {
    public String time;
    public int duration;
    public int totalPrice;

    // Required for Firebase
    public Booking() {}

    public Booking(String time, int duration, int totalPrice) {
        this.time = time;
        this.duration = duration;
        this.totalPrice = totalPrice;
    }
}
