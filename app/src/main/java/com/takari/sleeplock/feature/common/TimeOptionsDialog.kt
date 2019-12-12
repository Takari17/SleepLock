package com.takari.sleeplock.feature.common

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.jakewharton.rxrelay2.PublishRelay
import com.takari.sleeplock.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.time_selection_layout.*


class TimeOptionsDialog : DialogFragment() {

    private val timeInMillis = PublishRelay.create<Long>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =

        AlertDialog.Builder(activity).apply {
            var minutes = 0

            setTitle("Select a Time")

            setSingleChoiceItems(getTimeOptions(), -1) { _: Any, index: Int ->
                // 0 index means the user wants to select a custom time
                if (index == 0) {
                    dismiss()
                    showCustomTimeDialog()
                } else {
                    minutes = getTimeOptions()[index].extractInts()
                }

            }
            setPositiveButton("Set a Time") { _, _ ->
                timeInMillis.accept(minutes.minToMilli())
            }
            setNegativeButton("Cancel") { _, _ -> }
            setIcon(R.drawable.lightblueclock)
        }.create()


    private fun showCustomTimeDialog() = Dialog(context!!).apply {

        setContentView(R.layout.time_selection_layout)
        setTitle("Select a Time")

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

            val totalMinutes = hours.hrToMin(extraMinutes = minutes)

            timeInMillis.accept(totalMinutes.minToMilli())

            dismiss()
        }
    }.show()


    private fun getTimeOptions() = arrayOf(
        "Select Custom Time",
        "10 min",
        "20 min",
        "30 min",
        "40 min",
        "50 min",
        "60 min",
        "70 min",
        "80 min",
        "90 min"
    )

    fun getUserSelectedTime(): Observable<Long> = timeInMillis
}
