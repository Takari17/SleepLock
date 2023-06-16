package com.takari.sleeplock.whitenoise.ui

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.whitenoise.WhiteNoiseUiState
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.WhiteNoiseOptions
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

    fun getWhiteNoiseOptions(): List<WhiteNoise> {
        //todo look at that google project, look at how they handle the data layer when it's not needed
        return WhiteNoiseOptions.get
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(showTimePicker = false)
    }
}
