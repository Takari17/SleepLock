package com.takari.sleeplock.whitenoise.data.sounds

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AirPlane(private val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.airplane

    override fun name(): String = "Airplane"

    override fun description(): String =
        "Listen to the calm engines of an airplane as it soars above the sky."

    override fun sound(): Int = R.raw.airplane

    override fun colorHexCode(): String = "#ffb74d"
}