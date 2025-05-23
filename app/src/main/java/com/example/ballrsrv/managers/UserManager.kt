package com.example.ballrsrv.managers

import com.example.ballrsrv.models.User
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class UserManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun registerUser(email: String, password: String): Boolean {
        if (!User.isValidEmail(email)) {
            Log.e(TAG, "Invalid email format")
            return false
        }
        
        if (!User.isValidPassword(password)) {
            Log.e(TAG, "Invalid password format")
            return false
        }
        
        // Check if user already exists
        if (sharedPreferences.contains(email)) {
            Log.e(TAG, "User already exists")
            return false
        }
        
        // Store user credentials
        sharedPreferences.edit().apply {
            putString(email, password)
            apply()
        }
        
        return true
    }
    
    fun loginUser(email: String, password: String): Boolean {
        val storedPassword = sharedPreferences.getString(email, null)
        return storedPassword == password
    }
    
    fun getUser(email: String): User? {
        val password = sharedPreferences.getString(email, null) ?: return null
        return User(email, password)
    }
    
    companion object {
        private const val PREFS_NAME = "UserPrefs"
        private const val TAG = "UserManager"
    }
} 