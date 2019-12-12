package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoilingWater(val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.boilingwater

    override fun name(): String = "Boiling Water"

    override fun rawFile(): Int = R.raw.boilingwater
}