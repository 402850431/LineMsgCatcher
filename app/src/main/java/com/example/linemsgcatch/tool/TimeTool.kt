package com.example.linemsgcatch.tool

import java.text.SimpleDateFormat
import java.util.*

fun nowTimeFormatter(time: Long?): String {
    return SimpleDateFormat("MM/dd hh:mm:ss", Locale.getDefault()).format(time)
}

fun nowDateFormatter(time: Long?): String {
    return SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(time)
}