package com.takari.sleeplock.sleeptimer.admin

import android.app.admin.DevicePolicyManager
import android.media.AudioManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeviceSleeper @Inject constructor(
    private val audioManager: AudioManager,
    private val policyManager: DevicePolicyManager
) {

    /**Slowly mutes and sleeps the devices over a period of 30 seconds.*/
    val sleepDevice = flow<Unit> {
        for (i in 1..15) {

            if (i == 15) turnOffScreen() //completed

            lowerVolumeOnce()

            delay(2000) //2 seconds
        }
    }

    fun lowerVolumeOnce() {
        audioManager.adjustVolume(
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    fun turnOffScreen() {
        policyManager.lockNow()
    }
}