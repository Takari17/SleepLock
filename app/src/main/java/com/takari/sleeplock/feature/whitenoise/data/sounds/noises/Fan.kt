package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fan(val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.fan

    override fun name(): String = "Fan"

    override fun rawFile(): Int = R.raw.fan
}