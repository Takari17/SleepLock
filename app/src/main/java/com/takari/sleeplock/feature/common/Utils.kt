package com.takari.sleeplock.feature.common

import android.content.Context
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.dmoral.toasty.Toasty
import java.util.*


/**
Returns a lazy view model reference scoped to the Activity of a Fragment.
Used so Dagger can inject ViewModels with args.
 */
inline fun <reified T : ViewModel> Fragment.activityViewModelFactory(
    crossinline provider: () -> T
) = activityViewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider() as T
    }
}

fun Int.hrToMin(extraMinutes: Int = 0): Int = (this * 60) + extraMinutes

fun Int.minToMilli(): Long = (this * 60000).toLong()

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

fun showDialog(dialog: DialogFragment, fragmentManager: FragmentManager, tag: String) {
    if (!dialog.isAdded) dialog.show(fragmentManager, tag)
}

fun Context.showWarningToast(message: String) {
    Toasty.warning(this, message, Toasty.LENGTH_SHORT, true).show()
}
