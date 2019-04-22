package com.example.sleeplock.ui

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Select a Time")

        builder.setSingleChoiceItems(timeOptions, -1) { _, which ->
            // Replaces the current dialog with the new one
            if (which == 0) {
                dismiss()
                showCustomDialog()

            } else {
                val minutes = extractIntValues(timeOptions[which])
                timeInMillis = minutes.convertMinToMilli()
            }

        }

        builder.setPositiveButton("Set Time") { _, _ ->
            userSelectedTime.value = timeInMillis
        }

        builder.setNegativeButton("Cancel") { _, _ ->

        }

        builder.setIcon(R.drawable.lightblueclock)

        return builder.create()
    }


    private fun showCustomDialog() {

        val customDialog = Dialog(context!!)

        customDialog.setContentView(R.layout.custom_time_layout)
        customDialog.setTitle("Select a Time")

        val numPickerHours = customDialog.number_picker_hours
        val numPickerMinutes = customDialog.number_picker_minutes
        val setTimeButton = customDialog.set_time_button


        numPickerHours.maxValue = 23
        numPickerHours.minValue = 0

        numPickerMinutes.maxValue = 59
        numPickerMinutes.minValue = 0

        setTimeButton.setOnClickListener {

            val hours = numPickerHours.value
            val minutes = numPickerMinutes.value

            val totalMinutes = hours.convertHoursToMin(minutes)

            timeInMillis = totalMinutes.convertMinToMilli()

            userSelectedTime.value = timeInMillis
            customDialog.dismiss()
        }

        return customDialog.show()
    }

    // Separates the "Min" value from our array and returns only the Int value
    private fun extractIntValues(text: String): Int = Integer.valueOf(text.replace("[^0-9]".toRegex(), ""))

    fun getUserSelectedTime(): LiveData<Long> = userSelectedTime
}