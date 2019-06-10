package com.takari.sleeplock.ui.feature.whitenoise

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.takari.sleeplock.utils.ItemData

class WhiteNoise(
    private val mediaPlayer: MediaPlayer,
    context: Context,
    index: Int
) {

    private val soundList = ItemData.getAllSoundReferences()

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
        for (i in soundList.indices) {
            if (i == index) {
                mediaPlayer.apply {
                    setDataSource(context, Uri.parse("android.resource://com.takari.sleeplock/raw/${soundList[i]}"))
                    prepare()
                }
            }
        }
    }
}