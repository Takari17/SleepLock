package com.takari.sleeplock.sleeptimer.ui

sealed class SleepTimerViewCommands {
    object PauseService : SleepTimerViewCommands()
    object ResumeService : SleepTimerViewCommands()
    object DestroyService : SleepTimerViewCommands()
    object OpenTimeSelectionDialog : SleepTimerViewCommands()
}