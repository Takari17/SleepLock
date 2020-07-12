package com.takari.sleeplock.whitenoise.data.sounds

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OceanShore(private val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.oceanshore

    override fun name(): String = "Ocean Shore"

    override fun description(): String =
        "Listen to the crashing impact of waves on the seashore as you observe the beach."

    override fun sound(): Int = R.raw.oceanshore

    override fun colorHexCode(): String = "#fdd835"
}
