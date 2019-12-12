package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Helicopter(val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.helicopter

    override fun name(): String = "Helicopter"

    override fun rawFile(): Int = R.raw.helicopter
}
