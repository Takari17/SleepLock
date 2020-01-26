package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rain(val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.rain

    override fun name(): String = "Rain"

    override fun rawFile(): Int = R.raw.rain
}
