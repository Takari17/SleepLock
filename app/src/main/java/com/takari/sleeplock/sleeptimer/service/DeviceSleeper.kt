package com.takari.sleeplock.sleeptimer.service

import android.app.admin.DevicePolicyManager
import android.media.AudioManager
import com.takari.sleeplock.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeviceSleeper @Inject constructor(
    private val audioManager: AudioManager,
    private val policyManager: DevicePolicyManager
) {

    /**Slowly mutes and sleeps the devices over a period of 30 seconds.*/
    val sleepDevice = flow<Unit> {
        for (i in 1..20) {

            if (i == 20) turnOffScreen() //completed

            lowerVolumeOnce()

            delay(5000)
        }
    }

    fun lowerVolumeOnce() {
        log("lowerVolumeOnce()")
        audioManager.adjustVolume(
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    fun turnOffScreen() {
        log("Turning off screen")
        policyManager.lockNow()
    }
}