package com.takari.sleeplock.whitenoise.ui

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.shared.log
import com.takari.sleeplock.shared.to24HourFormat
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.allWhiteNoises
import kotlinx.coroutines.flow.MutableStateFlow

class WhiteNoiseViewModel : ViewModel() {

    val uiState = MutableStateFlow(WhiteNoiseUiState())
    var events: (WhiteNoiseOneTimeEvents) -> Unit = {}


    fun onWhiteNoiseItemClick(
        clickedWhiteNoise: WhiteNoise,
        serviceIsRunning: Boolean,
        timerIsRunning: Boolean
    ) {
        log("onWhiteNoiseItemClick: ${uiState.value}")

        when {
            serviceIsRunning and timerIsRunning -> {
                events(WhiteNoiseOneTimeEvents.PauseService)

                uiState.value = uiState.value.copy(
                    mediaServiceIsRunning = true,
                    clickedWhiteNoise = clickedWhiteNoise
                )
            }

            serviceIsRunning and !timerIsRunning -> {
                events(WhiteNoiseOneTimeEvents.ResumeService)

                uiState.value = uiState.value.copy(
                    mediaServiceIsRunning = true,
                    clickedWhiteNoise = clickedWhiteNoise

                )
            }

            !serviceIsRunning -> {
                events(WhiteNoiseOneTimeEvents.ShowTimePickerDialog)

                uiState.value = uiState.value.copy(clickedWhiteNoise = clickedWhiteNoise)
            }
        }
    }

    fun onUserSelectedTimeFromDialog(millis: Long) {
        if (millis != 0L) {
            uiState.value = uiState.value.copy(mediaServiceIsRunning = true)

            events(
                WhiteNoiseOneTimeEvents.StartAndBindToService(
                    millis = millis,
                    whiteNoise = uiState.value.clickedWhiteNoise
                )
            )
        }
    }

    fun setElapseTime(elapseTime: Long) {
        uiState.value = uiState.value.copy(elapseTime = elapseTime.to24HourFormat())
    }

    fun setIsTimerRunning(isRunning: Boolean) {
        uiState.value = uiState.value.copy(isTimerRunning = isRunning)
    }

    fun getWhiteNoiseList(): List<WhiteNoise> {
        //todo look at that google project, look at how they handle the data layer when it's not needed
        return allWhiteNoises
    }

    fun restoreState(state: WhiteNoiseUiState) {
        uiState.value = state
    }

    fun resetState() {
        // won't reset clickedWhiteNoise
        uiState.value = WhiteNoiseUiState(clickedWhiteNoise = uiState.value.clickedWhiteNoise)
    }

    fun destroyService() {
        events(WhiteNoiseOneTimeEvents.DestroyService)
    }
}
