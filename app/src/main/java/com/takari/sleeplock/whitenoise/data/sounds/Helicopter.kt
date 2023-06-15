package com.takari.sleeplock.whitenoise.data.sounds

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.parcelize.Parcelize

@Parcelize
data class Helicopter(private val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.helicopter

    override fun name(): String = "Helicopter"

    override fun description(): String =
        "Listen to the spinning rotor blades of a chopper as you fly through the sky."

    override fun sound(): Int = R.raw.helicopter

    override fun colorHexCode(): String = "#757575"
}
