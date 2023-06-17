package com.takari.sleeplock.whitenoise

import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.sounds.Rain
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService

data class WhiteNoiseUiState(
    val showTimePickerDialog: Boolean = false,
    val mediaServiceIsRunning: Boolean = false,
    val elapseTime: String = "00:00",
    val isTimerRunning: Boolean = WhiteNoiseService.timerIsRunning(),
    val clickedWhiteNoise: WhiteNoise = Rain()
)
