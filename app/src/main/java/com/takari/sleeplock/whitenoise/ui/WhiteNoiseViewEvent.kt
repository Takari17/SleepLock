package com.takari.sleeplock.whitenoise.ui

import com.takari.sleeplock.whitenoise.data.WhiteNoise

interface WhiteNoiseViewEvent {

    fun onAdapterClick(
        clickedWhiteNoise: WhiteNoise,
        serviceIsRunning: Boolean,
        timerIsRunning: Boolean
    )

    fun onUserSelectedTimeFromDialog(millis: Long)

    fun onResetButtonClick()
}