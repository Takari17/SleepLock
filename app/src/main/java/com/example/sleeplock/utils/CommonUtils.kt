@file:Suppress("UNCHECKED_CAST")

package com.example.sleeplock.utils

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sleeplock.R
import es.dmoral.toasty.Toasty
import java.util.*

/*
Higher order extension function that allows dagger to inject viewModels.
Scoped to the underlying activity, not the fragment itsself
 */
inline fun <reified T : ViewModel> Fragment.activityViewModelFactory(
    crossinline provider: () -> T
) = activityViewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider() as T
    }
}

// Conversion methods
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


// Toast methods
fun Boolean.warnOrSuccessToast(context: Context) {
    if (this) showWarningToast(context) else showSoundSelectedToast(context)
}

fun showSoundSelectedToast(context: Context) {
    Toasty.success(context, R.string.sound_selected, Toasty.LENGTH_SHORT, true).show()
}

fun showWarningToast(context: Context) {
    Toasty.warning(context, R.string.reset_the_timer, Toasty.LENGTH_SHORT, true).show()
}

fun showFinishedToast(context: Context, start: Boolean) {
    if (start) {
        Toasty.info(context, R.string.timer_finished, Toasty.LENGTH_SHORT, true).show()
    }
}

fun Context.getResourceString(id: Int): String = this.resources.getString(id)

fun Int.toUri(): Uri = Uri.parse("android.resource://com.example.sleeplock/raw/$$this")