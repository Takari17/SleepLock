package com.takari.sleeplock.whitenoise

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService

data class WhiteNoiseUiState(
    val showTimePickerDialog: Boolean = false,
    val mediaIsPlaying: Boolean = false,
    val elapseTime: String = "00:00",
    val isTimerRunning: Boolean = WhiteNoiseService.timerIsRunning(),
)
