package com.takari.sleeplock.sleeptimer.ui

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.shared.to24HourFormat
import kotlinx.coroutines.flow.MutableStateFlow


class SleepTimerViewModel : ViewModel() {

    val uiState = MutableStateFlow(SleepTimerUiState())
    var events: (SleepTimerViewCommands) -> Unit = {}

    fun onStartButtonClick(serviceIsRunning: Boolean, timerIsRunning: Boolean) {
        when {
            serviceIsRunning and timerIsRunning -> {
                events(SleepTimerViewCommands.PauseService)

                uiState.value = uiState.value.copy(timerServiceIsRunning = true)
            }

            serviceIsRunning and !timerIsRunning -> {
                events(SleepTimerViewCommands.ResumeService)

                uiState.value = uiState.value.copy(timerServiceIsRunning = true)
            }

            !serviceIsRunning -> events(SleepTimerViewCommands.ShowTimePickerDialog)
        }
    }

    fun onUserSelectedTimeFromDialog(millis: Long) {
        if (millis != 0L) {
            uiState.value = uiState.value.copy(
                timerServiceIsRunning = true,
                isTimerRunning = true,
                elapseTime = millis.to24HourFormat()
            )

            events(SleepTimerViewCommands.StartAndBindToService(millis = millis))
        }
    }

    fun setElapseTime(elapseTime: Long) {
        uiState.value = uiState.value.copy(elapseTime = elapseTime.to24HourFormat())
    }

    fun setIsTimerRunning(isRunning: Boolean) {
        uiState.value = uiState.value.copy(isTimerRunning = isRunning)
    }


    fun restoreState(state: SleepTimerUiState) {
        uiState.value = state
    }

    fun resetState() {
        uiState.value = SleepTimerUiState()
    }

    fun destroyService() {
        events(SleepTimerViewCommands.DestroyService)
    }
}
