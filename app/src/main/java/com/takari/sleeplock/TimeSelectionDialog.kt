package com.takari.sleeplock.shared

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.takari.sleeplock.R
import com.takari.sleeplock.hrToMilli
import com.takari.sleeplock.minToMilli

/**
 * The time pickers in Jetpack Compose are too limiting for my use case, and I don't want to write
 * some custom composable for a simple time picker. So I reverted back to standard XML here.
 */

class TimeSelectionDialog : DialogFragment() {

    var onTimeSelected: (milliseconds: Long) -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Dialog(requireContext()).apply {

            setContentView(R.layout.time_selection_layout)

            val numberPickerHours = findViewById<NumberPicker>(R.id.numberPickerHours)
            val numberPickerMin = findViewById<NumberPicker>(R.id.numberPickerMin)
            val setTimeButton = findViewById<Button>(R.id.setTimeButton)

            val numPickerHours = numberPickerHours.apply {
                maxValue = 23
                minValue = 0
            }
            val numPickerMinutes = numberPickerMin.apply {
                maxValue = 59
                minValue = 0
            }

            setTimeButton.setOnClickListener {

                val totalMilliSeconds =
                    numPickerHours.value.hrToMilli() + numPickerMinutes.value.minToMilli()

                onTimeSelected(totalMilliSeconds)

                dismiss()
            }
        }
}
