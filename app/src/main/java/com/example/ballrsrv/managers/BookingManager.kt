package com.example.ballrsrv.managers

import com.example.ballrsrv.data.Booking
import com.example.ballrsrv.data.BookingStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class BookingManager {
    private val db = FirebaseFirestore.getInstance()
    private val bookingsCollection = db.collection("bookings")

    suspend fun createBooking(booking: Booking): Result<Booking> = runCatching {
        val bookingMap = hashMapOf(
            "courtId" to booking.courtId,
            "userId" to booking.userId,
            "date" to booking.date,
            "startTime" to booking.startTime,
            "endTime" to booking.endTime,
            "status" to booking.status,
            "totalPrice" to booking.totalPrice
        )

        val docRef = bookingsCollection.add(bookingMap).await()
        booking.copy(id = docRef.id)
    }

    suspend fun getBookingsByUser(userId: String): Result<List<Booking>> = runCatching {
        bookingsCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Booking::class.java)
    }

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> = runCatching {
        bookingsCollection.document(bookingId)
            .update("status", status)
            .await()
    }
} 