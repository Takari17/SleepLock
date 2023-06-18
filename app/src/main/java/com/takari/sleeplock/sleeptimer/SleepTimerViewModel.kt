package com.takari.sleeplock.sleeptimer

import androidx.lifecycle.ViewModel
import com.takari.sleeplock.sleeptimer.ui.SleepTimerViewCommands

class SleepTimerViewModel : ViewModel() {

    //the view overrides this to receive events from this viewModel

    var events: (SleepTimerViewCommands) -> Unit = {}
//
//    fun onStartPauseButtonClick(serviceIsRunning: Boolean, timerIsRunning: Boolean) {
//        if (serviceIsRunning) {
//            if (timerIsRunning) viewCommand(SleepTimerViewCommands.PauseService)
//            else viewCommand(SleepTimerViewCommands.ResumeService)
//        } else viewCommand(SleepTimerViewCommands.OpenTimeSelectionDialog)
//    }
}
