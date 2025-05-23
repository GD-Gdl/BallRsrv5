package com.example.ballrsrv.models

data class User(
    val email: String,
    val password: String
) {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidPassword(password: String): Boolean {
            // Password should be at least 8 characters long and contain at least one number
            return password.length >= 8 && password.any { it.isDigit() }
        }
    }
} 