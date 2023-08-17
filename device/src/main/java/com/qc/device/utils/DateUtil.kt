package com.qc.device.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
    isLenient = false
}

fun Date.formatDate(): String  = try{
    dateFormat.format(this)
} catch (_: Exception) {
    ""
}

fun Long.formatDate(): String = try {
    val date = Date(this)
    dateFormat.format(date)
} catch (_: Exception) {
    ""
}

fun String.toDate(): Date = try {
    dateFormat.parse(this) ?: throw Exception("null date")
} catch (_: Exception) {
    Date(0)
}