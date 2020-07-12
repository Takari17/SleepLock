package com.takari.sleeplock.whitenoise.data.sounds

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeepSea(private val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.deepsea

    override fun name(): String = "Deep Sea"

    override fun description(): String =
        "Listen to the muffled sounds of the depths of the deepest sea"

    override fun sound(): Int = R.raw.deepsea

    override fun colorHexCode(): String = "#303f9f"
}
