package com.takari.sleeplock.whitenoise

import android.graphics.drawable.Drawable
import com.takari.sleeplock.R

data class WhiteNoiseUiState(
    val showTimePicker: Boolean = false,
    val mediaIsPlaying: Boolean = false,
    val mediaOption: Int = R.drawable.transparant_play_icon,
    val timer: String = "00:00"
)
