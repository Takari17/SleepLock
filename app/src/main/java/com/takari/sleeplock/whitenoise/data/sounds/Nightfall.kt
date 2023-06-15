package com.takari.sleeplock.whitenoise.data.sounds

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.parcelize.Parcelize

@Parcelize
data class Nightfall(private val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.nightfall

    override fun name(): String = "Nightfall"

    override fun description(): String =
        "Listen to the sounds of nature as nocturnal creatures awake from their slumber."

    override fun sound(): Int = R.raw.nightfall

    override fun colorHexCode(): String = "#283593"
}