package com.takari.sleeplock.whitenoise

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.logD
import com.takari.sleeplock.to24HourFormat
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.WhiteNoiseOptions
import com.takari.sleeplock.whitenoise.service.TimerFlow
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseOneTimeEvents
import kotlinx.coroutines.flow.MutableStateFlow

class WhiteNoiseViewModel : ViewModel() {

    val uiState = MutableStateFlow(WhiteNoiseUiState())
    var events: (WhiteNoiseOneTimeEvents) -> Unit = {}


    /*
    If the media isn't playing, then show time picker. If media is playing and the timer is running,
    then pause it. Else resume it.
     */
    fun onWhiteNoiseItemClick(clickedWhiteNoise: WhiteNoise) {
        logD("onWhiteNoiseItemClick: $uiState")

        when {
            uiState.value.mediaIsPlaying and uiState.value.isTimerRunning -> {
                events(WhiteNoiseOneTimeEvents.PauseService)

                uiState.value = uiState.value.copy(
                    showTimePickerDialog = false,
                    mediaIsPlaying = true,
                    clickedWhiteNoise = clickedWhiteNoise
                )
            }

            uiState.value.mediaIsPlaying and !uiState.value.isTimerRunning -> {
                events(WhiteNoiseOneTimeEvents.ResumeService)

                uiState.value = uiState.value.copy(
                    showTimePickerDialog = false,
                    mediaIsPlaying = true,
                    clickedWhiteNoise = clickedWhiteNoise

                )
            }

            !uiState.value.mediaIsPlaying -> {
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
                mediaIsPlaying = true,
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

    fun resetState() {
        uiState.value = WhiteNoiseUiState(
            clickedWhiteNoise = uiState.value.clickedWhiteNoise // won't reset this state
        )

        events(WhiteNoiseOneTimeEvents.DestroyService)
    }
}
