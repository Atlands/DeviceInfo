package com.qc.device.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))

fun Date.formatDate(): String  = try{
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.format(this)
} catch (_: Exception) {
    ""
}

fun Long.formatDate(): String = try {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = Date(this)
    dateFormat.format(date)
} catch (_: Exception) {
    ""
}

fun String.toDate(): Date = try {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.parse(this) ?: throw Exception("null date")
} catch (_: Exception) {
    Date(0)
}