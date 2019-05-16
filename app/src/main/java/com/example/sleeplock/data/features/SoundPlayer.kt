package com.example.sleeplock.data.features

import android.content.Context
import android.media.MediaPlayer
import com.example.sleeplock.utils.WHITE_NOISE


class SoundPlayer(context: Context, index: Int) {

    private lateinit var sound: MediaPlayer

    init { setSound(context, index) }

    fun start() {
        sound.start()
        sound.isLooping = true
    }

    fun pause() = sound.pause()

    fun reset() = sound.release()

    private fun setSound(context: Context, index: Int) {
        // Creates sound bases off position of Recycler View item click

        for (i in WHITE_NOISE.indices) {

            if (i == index) sound = MediaPlayer.create(context, WHITE_NOISE[i])
        }
    }
}