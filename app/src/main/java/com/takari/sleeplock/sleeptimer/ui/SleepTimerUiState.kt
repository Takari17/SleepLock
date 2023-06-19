package com.takari.sleeplock.sleeptimer.ui

import com.takari.sleeplock.sleeptimer.service.SleepTimerService

data class SleepTimerUiState(
    val showTimePickerDialog: Boolean = false,
    val timerServiceIsRunning: Boolean = false,
    val elapseTime: String = "00:00",
    val isTimerRunning: Boolean = SleepTimerService.timerIsRunning()
)
