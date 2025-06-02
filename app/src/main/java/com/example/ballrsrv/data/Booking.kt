package com.example.ballrsrv.data

import java.util.Date

data class Booking(
    val id: String = "",
    val courtId: String = "",
    val userId: String = "",
    val date: Date = Date(),
    val startTime: String = "",
    val endTime: String = "",
    val status: BookingStatus = BookingStatus.PENDING,
    val totalPrice: Double = 0.0
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
} 