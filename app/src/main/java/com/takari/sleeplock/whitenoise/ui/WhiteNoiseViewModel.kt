package com.takari.sleeplock.whitenoise.ui

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.log
import com.takari.sleeplock.to24HourFormat
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.WhiteNoiseOptions
import com.takari.sleeplock.whitenoise.service.TimerFlow
import kotlinx.coroutines.flow.MutableStateFlow

class WhiteNoiseViewModel : ViewModel() {

    val uiState = MutableStateFlow(WhiteNoiseUiState())
    var events: (WhiteNoiseOneTimeEvents) -> Unit = {}


    /*
    If the media isn't playing, then show time picker. If media is playing and the timer is running,
    then pause it. Else resume it.
     */
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
                    showTimePickerDialog = false,
                    mediaServiceIsRunning = true,
                    clickedWhiteNoise = clickedWhiteNoise
                )
            }

            serviceIsRunning and !timerIsRunning -> {
                events(WhiteNoiseOneTimeEvents.ResumeService)

                uiState.value = uiState.value.copy(
                    showTimePickerDialog = false,
                    mediaServiceIsRunning = true,
                    clickedWhiteNoise = clickedWhiteNoise

                )
            }

            !serviceIsRunning -> {
                uiState.value = uiState.value.copy(
                    showTimePickerDialog = true,
                    clickedWhiteNoise = clickedWhiteNoise
                )
            }
        }
    }

    fun onUserSelectedTimeFromDialog(millis: Long) {
        if (millis != 0L) {
            uiState.value = uiState.value.copy(
                showTimePickerDialog = false,
                mediaServiceIsRunning = true,
            )

            events(
                WhiteNoiseOneTimeEvents.StartAndBindToService(
                    millis = millis,
                    whiteNoise = uiState.value.clickedWhiteNoise
                )
            )
        }
    }

    fun setTimerState(timerState: TimerFlow.TimerState) {
        uiState.value = uiState.value.copy(
            elapseTime = timerState.elapseTime.to24HourFormat(),
            isTimerRunning = timerState.isTimerRunning
        )
    }

    fun getWhiteNoiseOptions(): List<WhiteNoise> {
        //todo look at that google project, look at how they handle the data layer when it's not needed
        return WhiteNoiseOptions.get
    }

    fun closeDialog() {
        uiState.value = uiState.value.copy(showTimePickerDialog = false)
    }

    fun restoreState(state: WhiteNoiseUiState) {
        uiState.value = state
    }

    fun resetState(){
        // won't reset clickedWhiteNoise
        uiState.value = WhiteNoiseUiState(clickedWhiteNoise = uiState.value.clickedWhiteNoise)
    }

    fun destroyService() {
        events(WhiteNoiseOneTimeEvents.DestroyService)
    }
}
