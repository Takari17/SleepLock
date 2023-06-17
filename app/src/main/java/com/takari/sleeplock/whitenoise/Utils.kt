package com.takari.sleeplock.whitenoise

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.Log
import java.util.Locale

fun Int.minToMilli(): Long = (this * 60000).toLong()

fun Int.hrToMilli(): Long = (this * 3.6e+6).toLong()

fun logD(message: String) = Log.d("logs", message)

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

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}
