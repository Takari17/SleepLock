package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PeacefulNight(val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.peacefulnight

    override fun name(): String = "Peaceful Night"

    override fun rawFile(): Int = R.raw.peacefulnight
}