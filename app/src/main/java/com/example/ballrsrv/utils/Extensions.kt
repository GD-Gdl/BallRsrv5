package com.example.ballrsrv.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun ImageView.loadImage(url: String?) {
    url?.let { Picasso.get().load(it).into(this) }
}

fun Date.formatToString(pattern: String = "MMM dd, yyyy"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

fun String.toDate(pattern: String = "MMM dd, yyyy"): Date? {
    return try {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
} 