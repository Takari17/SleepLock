package com.takari.sleeplock.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.takari.sleeplock.R
import com.takari.sleeplock.utils.convertHoursToMin
import com.takari.sleeplock.utils.convertMinToMilli
import com.takari.sleeplock.utils.getResourceString
import kotlinx.android.synthetic.main.custom_time_layout.*

/**
Dialog that prompts the user with various time options.
 */
class TimeOptionDialog : DialogFragment() {

    private val userSelectedTime = MutableLiveData<Long>()
    private var timeInMillis: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =

        AlertDialog.Builder(activity).apply {

            setTitle(getResourceString(context, R.string.select_a_time))

            val timeOptions = getTimeOptions(context)

            setSingleChoiceItems(timeOptions, -1) { _, index ->

                if (index == 0) { // when index is 0 that means the user is gonna select a custom time so we open a new dialog
                    dismiss()
                    showCustomTimeDialog()

                } else {
                    val minutes = extractIntValues(timeOptions[index])
                    timeInMillis = minutes.convertMinToMilli()
                }
            }

            setPositiveButton(getResourceString(context, R.string.set_time)) { _, _ ->
                userSelectedTime.value = timeInMillis
            }

            setNegativeButton(getResourceString(context, R.string.cancel)) { _, _ -> }

            setIcon(R.drawable.lightblueclock)

        }.create()


    private fun showCustomTimeDialog() = Dialog(context!!).apply {

        setContentView(R.layout.custom_time_layout)
        setTitle(getResourceString(context, R.string.select_a_time))

        val numPickerHours = numberPickerHours.apply {
            maxValue = 23
            minValue = 0
        }
        val numPickerMinutes = numberPickerMin.apply {
            maxValue = 59
            minValue = 0
        }

        setTimeButton.setOnClickListener {

            val hours = numPickerHours.value
            val minutes = numPickerMinutes.value

            val totalMinutes = hours.convertHoursToMin(extraMinutes = minutes)

            timeInMillis = totalMinutes.convertMinToMilli()

            userSelectedTime.value = timeInMillis
            dismiss()
        }
    }.show()

    /**
    Removes any letters from a string and returns only the Int values.
     */
    private fun extractIntValues(text: String): Int =
        Integer.valueOf(text.replace("[^0-9]".toRegex(), ""))


    fun getUserSelectedTime(): LiveData<Long> = userSelectedTime


    private fun getTimeOptions(context: Context) = arrayOf(
        getResourceString(context, R.string.customTime),
        getResourceString(context, R.string.tenMin),
        getResourceString(context, R.string.twentyMin),
        getResourceString(context, R.string.thirtyMin),
        getResourceString(context, R.string.fortyMin),
        getResourceString(context, R.string.fiftyMin),
        getResourceString(context, R.string.sixtyMin),
        getResourceString(context, R.string.seventyMin),
        getResourceString(context, R.string.eightyMin),
        getResourceString(context, R.string.ninetyMin)
    )
}