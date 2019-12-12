package com.takari.sleeplock.feature.whitenoise.ui

import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import com.takari.sleeplock.feature.common.ButtonStateColor
import com.takari.sleeplock.feature.common.Timer
import javax.inject.Inject

data class WhiteNoiseViewState @Inject constructor(
    val currentTime: Long,
    val whiteNoise: WhiteNoise?,
    val color: ButtonStateColor,
    val isEnabled: Boolean,
    val timerAction: Timer.Action
)