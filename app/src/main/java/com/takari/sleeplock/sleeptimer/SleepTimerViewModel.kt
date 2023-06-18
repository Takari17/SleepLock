package com.takari.sleeplock.sleeptimer

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.sleeptimer.ui.SleepTimerViewCommands
import com.takari.sleeplock.to24HourFormat
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseUiState
import kotlinx.coroutines.flow.MutableStateFlow


class SleepTimerViewModel : ViewModel() {

    val uiState = MutableStateFlow(SleepTimerUiState())
    var events: (SleepTimerViewCommands) -> Unit = {}

    fun onStartButtonClick(serviceIsRunning: Boolean, timerIsRunning: Boolean) {
        when {
            serviceIsRunning and timerIsRunning -> {
                events(SleepTimerViewCommands.PauseService)

                uiState.value = uiState.value.copy(
                    showTimePickerDialog = false,
                    timerServiceIsRunning = true,
                )
            }

            serviceIsRunning and !timerIsRunning -> {
                events(SleepTimerViewCommands.ResumeService)

                uiState.value = uiState.value.copy(
                    showTimePickerDialog = false,
                    timerServiceIsRunning = true,
                )
            }

            !serviceIsRunning -> {
                uiState.value = uiState.value.copy(showTimePickerDialog = true)
            }
        }
    }

    fun onUserSelectedTimeFromDialog(millis: Long) {
        if (millis != 0L) {
            uiState.value = uiState.value.copy(
                showTimePickerDialog = false,
                timerServiceIsRunning = true,
                isTimerRunning = true,
                elapseTime = millis.to24HourFormat()
            )

            events(SleepTimerViewCommands.StartAndBindToService(millis = millis))
        }
    }

    fun closeDialog() {
        uiState.value = uiState.value.copy(showTimePickerDialog = false)
    }

    fun resetState(){
        // won't reset clickedWhiteNoise
        uiState.value = SleepTimerUiState()
    }
}
