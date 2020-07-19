package com.takari.sleeplock.sleeptimer.admin

import android.app.admin.DevicePolicyManager
import android.media.AudioManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


/*
After doing hours of research we have to use Device Admins to turn off the screen. All of the other
methods don't work and or have become useless and deprecated. This feels like such overkill for
this app :/
 */
class DeviceSleeper @Inject constructor(
    private val audioManager: AudioManager,
    private val policyManager: DevicePolicyManager
) {

    /**Slowly mutes and sleeps the devices over a period of 30 seconds.*/
    val slowlySleepDeviceFlow = flow<Unit> {
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