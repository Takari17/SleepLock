package com.example.sleeplock.feature

import android.content.Context
import android.media.MediaPlayer
import com.example.sleeplock.model.util.DataSource


class SoundPlayer(context: Context, index: Int) {

    private lateinit var sound: MediaPlayer

    init { setSound(context, index) }

    fun startMediaPlayer() {
        sound.start()
        sound.isLooping = true
    }

    fun pauseMediaPlayer() = sound.pause()

    fun resetMediaPlayer() = sound.release()


    private fun setSound(context: Context, index: Int) {
        // Creates sound bases off position of Recycler View item click, call before you play your sound

        val dataSource = DataSource()

        for (i in dataSource.WHITE_NOISE.indices) {

            if (i == index) sound = MediaPlayer.create(context, dataSource.WHITE_NOISE[i])
        }
    }
}