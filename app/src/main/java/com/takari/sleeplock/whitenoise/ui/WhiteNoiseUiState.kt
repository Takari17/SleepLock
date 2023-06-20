package com.takari.sleeplock.whitenoise.ui

import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.sounds.Rain
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService

data class WhiteNoiseUiState(
    val mediaServiceIsRunning: Boolean = false,
    val elapseTime: String = "00:00",
    val isTimerRunning: Boolean = WhiteNoiseService.timerIsRunning(),
    val clickedWhiteNoise: WhiteNoise = Rain()
)
