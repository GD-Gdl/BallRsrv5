package com.example.ballrsrv.data

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER
)

enum class UserRole {
    CUSTOMER,
    ADMIN
} 