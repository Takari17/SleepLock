package com.takari.sleeplock.whitenoise

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.WhiteNoiseOptions
import com.takari.sleeplock.whitenoise.data.sounds.Rain
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseViewCommands
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WhiteNoiseViewModel : ViewModel() {

    private var clickedWhiteNoise: WhiteNoise? = null

    //the view overrides this to receive events from this viewModel
    var viewCommand: (WhiteNoiseViewCommands) -> Unit = {}

    private val _uiState = MutableStateFlow(WhiteNoiseUiState())
    val uiState: StateFlow<WhiteNoiseUiState> = _uiState.asStateFlow()


    fun onAdapterClick(
        clickedWhiteNoise: WhiteNoise,
        serviceIsRunning: Boolean,
        timerIsRunning: Boolean
    ) {
        this.clickedWhiteNoise = clickedWhiteNoise

        if (serviceIsRunning) {
            if (timerIsRunning) viewCommand(WhiteNoiseViewCommands.PauseService)
            else viewCommand(WhiteNoiseViewCommands.ResumeService)
        } else {
            _uiState.value = _uiState.value.copy(showTimePicker = true)
        }
    }

    fun onUserSelectedTimeFromDialog(millis: Long) {
        if (millis != 0L) {
            _uiState.value = _uiState.value.copy(
                showTimePicker = false,
                mediaIsPlaying = true,
                mediaOption = R.drawable.transparant_pause_icon
            )

            viewCommand(WhiteNoiseViewCommands.StartAnimation)
            viewCommand(
                WhiteNoiseViewCommands.StartAndBindToService(
                    millis,
                    clickedWhiteNoise ?: Rain()
                )
            )
        }
    }

    fun getWhiteNoiseOptions(): List<WhiteNoise> {
        //todo look at that google project, look at how they handle the data layer when it's not needed
        return WhiteNoiseOptions.get
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(showTimePicker = false)
    }

    fun resetState() {
        clickedWhiteNoise = null
        _uiState.value = WhiteNoiseUiState()
    }
}
