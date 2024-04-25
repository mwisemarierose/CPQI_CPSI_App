package com.example.tnsapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
fun formatDate(dateStr: String): String? {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
    val outputFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = inputFormat.parse(dateStr)

    return date?.let { outputFormat.format(it) }
}

fun isTodayDate(dateStr: String): Boolean {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val formattedDate = formatDate(dateStr)

    return formattedDate == today
}