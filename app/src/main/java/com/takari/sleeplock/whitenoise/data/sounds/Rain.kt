package com.takari.sleeplock.whitenoise.data.sounds

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rain(private val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.rain

    override fun name(): String = "Rain"

    override fun description(): String =
        "Listen to rain droplets as a thundering storm passes by."

    override fun sound(): Int = R.raw.rain

    override fun colorHexCode(): String = "#1565c0"
}
