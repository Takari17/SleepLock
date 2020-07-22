package com.takari.sleeplock.whitenoise.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.sounds.Rain
import com.takari.sleeplock.shared.TimerAction

class WhiteNoiseViewModel : ViewModel(), WhiteNoiseViewEvents {

    private val _timerActionIcon = MutableLiveData(TimerAction.Play)
    val timerActionIcon: LiveData<TimerAction> = _timerActionIcon

    private var clickedWhiteNoise: WhiteNoise? = null
    var isViewBindedToService = false

    //the view overrides this to receive events from this viewModel
    var viewCommand: (WhiteNoiseViewCommands) -> Unit = {}


    override fun onAdapterClick(
        clickedWhiteNoise: WhiteNoise,
        serviceIsRunning: Boolean,
        timerIsRunning: Boolean
    ) {
        this.clickedWhiteNoise = clickedWhiteNoise

        if (serviceIsRunning) {
            if (timerIsRunning) viewCommand(WhiteNoiseViewCommands.PauseService)
            else viewCommand(WhiteNoiseViewCommands.ResumeService)
        } else viewCommand(WhiteNoiseViewCommands.OpenTimeSelectionDialog)
    }

    override fun onUserSelectedTimeFromDialog(millis: Long) {

        if (millis != 0L) {
            viewCommand(WhiteNoiseViewCommands.StartAnimation)
            viewCommand(WhiteNoiseViewCommands.StartAndBindToService(millis, clickedWhiteNoise ?: Rain()))
        }
    }

    override fun onResetButtonClick() {
        viewCommand(WhiteNoiseViewCommands.DestroyService)
    }

    /*
    Alts between pause and play icons, but I used an enum since I didn't want to reference a Drawable or
    resource id. The VM should know nothing about Android.
     */
    fun setTimerActionIcon(isTimerRunning: Boolean) {
        _timerActionIcon.value = if (isTimerRunning) TimerAction.Pause else TimerAction.Play
    }

    fun resetState() {
        _timerActionIcon.value = TimerAction.Play
        clickedWhiteNoise = null
        isViewBindedToService = false
    }
}
