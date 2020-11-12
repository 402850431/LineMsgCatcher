package com.example.linemsgcatch.tool

import java.text.SimpleDateFormat
import java.util.*


val todayDate = nowDateFormatter(System.currentTimeMillis())

fun nowTimeFormatter(time: Long?): String {
//    return SimpleDateFormat("MM/dd  a hh:mm", Locale.getDefault()).format(time)
    return SimpleDateFormat("a hh:mm", Locale.getDefault()).format(time)
}

fun nowDateFormatter(time: Long?): String {
    return SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(time)
}

fun dateMinus(minusDate: Int): String {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, -minusDate)
    return dateFormat.format(cal.time)
//    return SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(dateFormat)
}