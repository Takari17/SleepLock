package com.takari.sleeplock.shared

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.takari.sleeplock.R
import kotlinx.android.synthetic.main.time_selection_layout.*


class TimeSelectionDialog : DialogFragment() {

    var onTimeSelected: (milliseconds: Long) -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Dialog(requireContext()).apply {

            setContentView(R.layout.time_selection_layout)

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
