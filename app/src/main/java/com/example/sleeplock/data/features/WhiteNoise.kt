package com.example.sleeplock.data.features

import android.content.Context
import android.media.MediaPlayer
import com.example.sleeplock.utils.WHITE_NOISE
import com.example.sleeplock.utils.toUri
import javax.inject.Inject

class WhiteNoise @Inject constructor(
    private val mediaPlayer: MediaPlayer,
    context: Context,
    index: Int
) {

    init {
        setSound(context, index)
    }

    fun start() {
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    fun pause() = mediaPlayer.pause()

    fun reset() = mediaPlayer.release()

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