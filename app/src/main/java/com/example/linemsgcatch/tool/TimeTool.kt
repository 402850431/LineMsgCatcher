package com.example.linemsgcatch.tool

import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.milliseconds


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

fun apiTimeToMyTimeFormat (time: String): String {
    //TODO Cheryl: 格式不知道對不對
    val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()) //2020-11-16T05:59:11.101Z
    val apiDateTime = apiDateFormat.parse(time)
    return SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(apiDateTime)
}

fun apiTimeToTimeMillis (time: String): Long {
    val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()) //2020-11-16T05:59:11.101Z
    apiDateFormat.timeZone = TimeZone.getTimeZone("UTC+8")
    val apiDateTime = apiDateFormat.parse(time)
    return apiDateTime.time //.milliseconds
}