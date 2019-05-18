package com.example.sleeplock.data.features

import android.content.Context
import android.media.MediaPlayer
import com.example.sleeplock.utils.WHITE_NOISE
import com.example.sleeplock.utils.toUri

class WhiteNoise(
    private val mediaPlayer: MediaPlayer,
    context: Context,
    index: Int
) : Operable {

    init {
        setSound(context, index)
    }

    override fun start() {
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    override fun pause() = mediaPlayer.pause()

    override fun reset() = mediaPlayer.release()

    private fun setSound(context: Context, index: Int) {
        // Creates sound bases off position of Recycler View item click
        for (i in WHITE_NOISE.indices) {
            if (i == index) {
                mediaPlayer.apply {
                    setDataSource(context, WHITE_NOISE[i].toUri())
                    prepareAsync()
                }
            }
        }
    }
}