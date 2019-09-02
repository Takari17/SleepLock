@file:Suppress("UNCHECKED_CAST")

package com.takari.sleeplock.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

/*
 * Helper methods used throughout the codebase.
 */


/**
Factory that returns a lazy view model reference scoped to the underlying activity, not the fragment itsself.
 */
inline fun <reified T : ViewModel> Fragment.activityViewModelFactory(
    crossinline provider: () -> T
) = activityViewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider() as T
    }
}


fun Int.convertHoursToMin(extraMinutes: Int = 0): Int = (this * 60) + extraMinutes


fun Int.convertMinToMilli(): Long = (this * 60000).toLong()


fun Long.convertMilliToSeconds(): Int = (this / 1000).toInt()


fun Long.milliTo24HourFormat(): String {
    val seconds = (this / 1000 % 60).toInt()
    val minutes = (this / (1000 * 60) % 60).toInt()
    val hours = (this / (1000 * 60 * 60) % 24).toInt()

    return if (hours < 1) {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}


fun getResourceString(context: Context, id: Int): String = context.resources.getString(id)