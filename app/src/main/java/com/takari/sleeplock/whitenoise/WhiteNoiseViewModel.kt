package com.takari.sleeplock.whitenoise

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.WhiteNoiseOptions
import com.takari.sleeplock.whitenoise.data.sounds.Rain
import com.takari.sleeplock.whitenoise.service.TimerFlow
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseOneTimeEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.log

class WhiteNoiseViewModel : ViewModel() {

    private var clickedWhiteNoise: WhiteNoise? = null

    //the view overrides this to receive events from this viewModel
    var events: (WhiteNoiseOneTimeEvents) -> Unit = {}

    private val _uiState = MutableStateFlow(WhiteNoiseUiState())
    val uiState: StateFlow<WhiteNoiseUiState> = _uiState.asStateFlow()


    /*
    If the media isn't playing, then show time picker. If media is playing and the timer is running,
    then pause it. Else resume it.
     */
    fun onWhiteNoiseItemClick(clickedWhiteNoise: WhiteNoise) {
        logD("onWhiteNoiseItemClick: $uiState")
//        this.clickedWhiteNoise = clickedWhiteNoise //todo why is this needed?

        when {
            uiState.value.mediaIsPlaying and uiState.value.isTimerRunning -> {
                events(WhiteNoiseOneTimeEvents.PauseService)

                _uiState.value = _uiState.value.copy(
                    showTimePickerDialog = false,
                    mediaIsPlaying = true,
                )
            }

            uiState.value.mediaIsPlaying and !uiState.value.isTimerRunning -> {
                events(WhiteNoiseOneTimeEvents.ResumeService)

                _uiState.value = _uiState.value.copy(
                    showTimePickerDialog = false,
                    mediaIsPlaying = true,
                )
            }

            !uiState.value.mediaIsPlaying -> {
                _uiState.value = _uiState.value.copy(showTimePickerDialog = true)
            }
        }
    }

    fun onUserSelectedTimeFromDialog(millis: Long) {
        if (millis != 0L) {
            _uiState.value = _uiState.value.copy(
                showTimePickerDialog = false,
                mediaIsPlaying = true,
            )

            events(
                WhiteNoiseOneTimeEvents.StartAndBindToService(
                    millis,
                    clickedWhiteNoise ?: Rain()
                )
            )
        }
    }

    fun setTimerState(timerState: TimerFlow.TimerState) {
        _uiState.value = _uiState.value.copy(
            elapseTime = timerState.elapseTime.to24HourFormat(),
            isTimerRunning = timerState.isTimerRunning
        )
    }

    fun getWhiteNoiseOptions(): List<WhiteNoise> {
        //todo look at that google project, look at how they handle the data layer when it's not needed
        return WhiteNoiseOptions.get
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(showTimePickerDialog = false)
    }

    fun resetState() {
        clickedWhiteNoise = null
        _uiState.value = WhiteNoiseUiState()
    }
}
