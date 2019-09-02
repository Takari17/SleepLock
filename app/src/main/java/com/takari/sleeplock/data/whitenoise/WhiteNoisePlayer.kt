package com.takari.sleeplock.data.whitenoise

import android.content.Context
import android.media.MediaPlayer

class WhiteNoisePlayer(whiteNoise: Int, context: Context) {

    private val mediaPlayer = MediaPlayer.create(context, whiteNoise)

    fun start() {
        mediaPlayer.start()
        mediaPlayer.isLooping = true
    }

    fun pause() = mediaPlayer.pause()

    fun reset() = mediaPlayer.release()

}