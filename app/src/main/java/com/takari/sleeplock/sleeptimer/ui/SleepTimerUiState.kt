package com.takari.sleeplock.sleeptimer.ui

data class SleepTimerUiState(
    val showTimePickerDialog: Boolean = false,
    val timerServiceIsRunning: Boolean = false,
    val elapseTime: String = "00:00",
    val isTimerRunning: Boolean = false, //todo set default value to  SleepTimerService.timerIsRunning()
)
