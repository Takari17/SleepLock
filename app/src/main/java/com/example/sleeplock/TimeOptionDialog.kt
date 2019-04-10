package com.example.sleeplock

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.custom_time_layout.*


class TimeOptionDialog : DialogFragment() {

    private var timeInMillis: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Select a Time")

        val dataSource = DataSource()

        val timeOptions = dataSource.timeOptions

        builder.setSingleChoiceItems(timeOptions, -1, DialogInterface.OnClickListener { dialog, which ->
            // Replaces the current dialog with the new one
            if (which == 0) {
                dismiss()
                showCustomDialog()

            } else {
                val minutes = extractIntValues(timeOptions[which])
                timeInMillis = minutes.convertMinToMilli()
            }

        })

        builder.setPositiveButton("Set Time", DialogInterface.OnClickListener { dialog, which ->
            dialogTime.onNext(timeInMillis)
            Log.d("myLog", "dialog called 1...")

        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

        })

        builder.setIcon(R.drawable.clockicon) // todo: make the clock light blue

        return builder.create()
    }


    private fun showCustomDialog() {

        val customDialog = Dialog(activity)

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

            dialogTime.onNext(timeInMillis)
            Log.d("myLog", "dialog called 2 ...")

            customDialog.dismiss()
        }

        return customDialog.show()
    }


    // Separates the "Min" value from our array and returns only the Int value
    private fun extractIntValues(text: String): Int = Integer.valueOf(text.replace("[^0-9]".toRegex(), ""))


}