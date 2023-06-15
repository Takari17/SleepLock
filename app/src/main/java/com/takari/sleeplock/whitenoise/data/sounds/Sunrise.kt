package com.takari.sleeplock.whitenoise.data.sounds

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sunrise(private val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.sunrise

    override fun name(): String = "Sunrise"

    override fun description(): String =
        "Listen to the sounds of nature as the sun rises and a day starts anew."

    override fun sound(): Int = R.raw.sunrise

    override fun colorHexCode(): String = "#ffff00"
}
