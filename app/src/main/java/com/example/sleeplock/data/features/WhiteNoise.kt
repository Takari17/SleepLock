package com.example.sleeplock.data.features

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.sleeplock.utils.WHITE_NOISE

/*
 * Plays a sound from the "WHITE_NOISE" constant depending on the index passed through the constructor.
 */
class WhiteNoise(
    private val mediaPlayer: MediaPlayer,
    context: Context,
    index: Int
) {

    init {
        setSound(context, index)
    }

    fun start() {
        mediaPlayer.start()
        mediaPlayer.isLooping = true
    }

    fun pause() = mediaPlayer.pause()

    fun reset() = mediaPlayer.release()

    // Creates sound bases off position of Recycler View item click
    private fun setSound(context: Context, index: Int) {
        for (i in WHITE_NOISE.indices) {
            if (i == index) {
                mediaPlayer.apply {
                    setDataSource(context, Uri.parse("android.resource://com.example.sleeplock/raw/${WHITE_NOISE[i]}"))
                    prepare()
                }
            }
        }
    }
}