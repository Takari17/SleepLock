package com.takari.sleeplock.sleeptimer.ui

sealed class SleepTimerViewCommands {
    data class StartAndBindToService(val millis: Long) : SleepTimerViewCommands()
    object PauseService : SleepTimerViewCommands()
    object ResumeService : SleepTimerViewCommands()
    object DestroyService : SleepTimerViewCommands()
}