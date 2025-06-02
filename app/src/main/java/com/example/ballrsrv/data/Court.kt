package com.example.ballrsrv.data

data class Court(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val isAvailable: Boolean = true
) 