package com.example.sleeplock.model.util

import android.content.Context
import com.example.sleeplock.R
import es.dmoral.toasty.Toasty
import java.util.*


fun Int.convertHoursToMin(extraMinutes: Int = 0): Int = (this * 60) + extraMinutes

fun Int.convertMinToMilli(): Long = (this * 60000).toLong()

fun Long.convertMilliToSeconds(): Int = (this / 1000).toInt()


fun Long.formatTime(): String {
    val seconds = (this / 1000 % 60).toInt()
    val minutes = (this / (1000 * 60) % 60).toInt()
    val hours = (this / (1000 * 60 * 60) % 24).toInt()

    return if (hours < 1) {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}

fun showFinishedToast(context: Context){ Toasty.info(context, R.string.timer_finished, Toasty.LENGTH_SHORT, true).show() }



