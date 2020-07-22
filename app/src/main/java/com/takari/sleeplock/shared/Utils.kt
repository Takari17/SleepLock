package com.takari.sleeplock.shared

import android.util.Log
import java.util.*


fun Int.hrToMin(extraMinutes: Int = 0): Int = (this * 60) + extraMinutes

fun Int.minToMilli(): Long = (this * 60000).toLong()

fun Int.hrToMilli(): Long = (this * 3.6e+6).toLong()

fun Long.milliToSeconds(): Int = (this / 1000).toInt()

fun String.extractInts(): Int = Integer.valueOf(this.replace("[^0-9]".toRegex(), ""))

fun Long.to24HourFormat(): String {
    val seconds = (this / 1000 % 60).toInt()
    val minutes = (this / (1000 * 60) % 60).toInt()
    val hours = (this / (1000 * 60 * 60) % 24).toInt()

    return if (hours < 1) {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}

fun logD(message: String) = Log.d("zwi", message)