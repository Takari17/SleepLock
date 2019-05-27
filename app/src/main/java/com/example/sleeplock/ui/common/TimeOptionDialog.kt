package com.example.sleeplock.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.R
import com.example.sleeplock.utils.convertHoursToMin
import com.example.sleeplock.utils.convertMinToMilli
import com.example.sleeplock.utils.timeOptions
import kotlinx.android.synthetic.main.custom_time_layout.*


class TimeOptionDialog : DialogFragment() {

    private val userSelectedTime = MutableLiveData<Long>()
    private var timeInMillis: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =

        AlertDialog.Builder(activity).apply {

            setTitle("Select a Time")

            setSingleChoiceItems(timeOptions, -1) { _, which ->
                /*
                When which equals 0 that means the user selected the "Select Custom Time" dialog, so this dismisses
                the current dialog and shows the new one.
                 */
                if (which == 0) {
                    dismiss()
                    showCustomTimeDialog()

                } else {
                    val minutes = extractIntValues(timeOptions[which])
                    timeInMillis = minutes.convertMinToMilli()
                }

            }

            setPositiveButton("Set Time") { _, _ ->
                userSelectedTime.value = timeInMillis
            }

            setNegativeButton("Cancel") { _, _ -> }

            setIcon(R.drawable.lightblueclock)

        }.create()


    private fun showCustomTimeDialog() {

        val customDialog = Dialog(context!!).apply {
            setContentView(R.layout.custom_time_layout)
            setTitle("Select a Time")

        }

        val numPickerHours = customDialog.numberpickerHours.apply {
            maxValue = 23
            minValue = 0
        }
        val numPickerMinutes = customDialog.numberPickerMin.apply {
            maxValue = 59
            minValue = 0
        }

        customDialog.setTimeButton.setOnClickListener {

            val hours = numPickerHours.value
            val minutes = numPickerMinutes.value

            val totalMinutes = hours.convertHoursToMin(minutes)

            timeInMillis = totalMinutes.convertMinToMilli()

            userSelectedTime.value = timeInMillis
            customDialog.dismiss()
        }

        return customDialog.show()
    }

    // Removes any letters from a string and returns only the Int values.
    private fun extractIntValues(text: String): Int = Integer.valueOf(text.replace("[^0-9]".toRegex(), ""))

    fun getUserSelectedTime(): LiveData<Long> = userSelectedTime
}