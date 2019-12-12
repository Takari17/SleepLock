package com.takari.sleeplock.feature.sleeptimer.admin

import android.media.AudioManager
import javax.inject.Inject

class VolumeManager @Inject constructor(
    private val audioManager: AudioManager
) {

    fun lowerVolume() {
        audioManager.adjustVolume(
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_PLAY_SOUND
        )
    }
}
