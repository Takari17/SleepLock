package com.takari.sleeplock.data.feature

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class WhiteNoisePlayer(
    private val mediaPlayer: MediaPlayer,
    private val whiteNoise: Int,
    context: Context
) {

    init {
        setSound(context)
    }

    fun start() {
        mediaPlayer.start()
        mediaPlayer.isLooping = true
    }

    fun pause() = mediaPlayer.pause()

    fun reset() = mediaPlayer.release()

    private fun setSound(context: Context) {
        mediaPlayer.apply {
            setDataSource(
                context,
                Uri.parse("android.resource://com.takari.sleeplock/raw/$whiteNoise")
            )
            prepare()
        }
    }
}