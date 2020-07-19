package com.takari.sleeplock.sleeptimer.ui

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseViewCommands
import javax.inject.Inject


class SleepTimerViewModel @Inject constructor() : ViewModel() {

    //the view overrides this to receive events from this viewModel
    var viewCommand: (SleepTimerViewCommands) -> Unit = {}

    fun onStartPauseButtonClick(serviceIsRunning: Boolean, timerIsRunning: Boolean) {
        if (serviceIsRunning) {
            if (timerIsRunning) viewCommand(SleepTimerViewCommands.PauseService)
            else viewCommand(SleepTimerViewCommands.ResumeService)
        } else viewCommand(SleepTimerViewCommands.OpenTimeSelectionDialog)
    }
}
