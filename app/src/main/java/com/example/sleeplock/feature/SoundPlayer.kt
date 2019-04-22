package com.example.sleeplock.feature

import android.content.Context
import android.media.MediaPlayer
import com.example.sleeplock.utils.WHITE_NOISE


class SoundPlayer(context: Context, index: Int) {

    private lateinit var sound: MediaPlayer

    init { setSound(context, index) }

    // todo change to just start, pause and reset, same for the timer
    fun startMediaPlayer() {
        sound.start()
        sound.isLooping = true
    }

    fun pauseMediaPlayer() = sound.pause()

    fun resetMediaPlayer() = sound.release()


    private fun setSound(context: Context, index: Int) {
        // Creates sound bases off position of Recycler View item click, call before you play your sound

        for (i in WHITE_NOISE.indices) {

            if (i == index) sound = MediaPlayer.create(context, WHITE_NOISE[i])
        }
    }
}