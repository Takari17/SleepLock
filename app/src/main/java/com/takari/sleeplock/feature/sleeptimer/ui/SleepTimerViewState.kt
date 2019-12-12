package com.takari.sleeplock.feature.sleeptimer.ui

import com.takari.sleeplock.feature.common.ButtonStateColor
import com.takari.sleeplock.feature.common.Timer
import javax.inject.Inject

data class SleepTimerViewState@Inject constructor(
    val currentTime: Long,
    val color: ButtonStateColor,
    val isEnabled: Boolean,
    val timerAction: Timer.Action
)