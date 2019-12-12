package com.takari.sleeplock.feature.whitenoise.events

import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import com.takari.sleeplock.feature.common.Animate

/**
Represents events WhiteNoiseViewModel wants WhiteNoiseFragment to perform only once. In
 other words use a PublishRelay for this.
 */
sealed class WhiteNoiseSingleEvent {
    data class StartAnimation(val duration: Animate.Duration) : WhiteNoiseSingleEvent()
    object ReverseAnimation : WhiteNoiseSingleEvent()
    object OpenSoundDialog : WhiteNoiseSingleEvent()
    data class ShowWarningToast(val message: String) : WhiteNoiseSingleEvent()
    data class StartService(val millis: Long, val whiteNoise: WhiteNoise) : WhiteNoiseSingleEvent()
    object PauseService : WhiteNoiseSingleEvent()
    object ResumeService : WhiteNoiseSingleEvent()
    object ResetService : WhiteNoiseSingleEvent()
}
