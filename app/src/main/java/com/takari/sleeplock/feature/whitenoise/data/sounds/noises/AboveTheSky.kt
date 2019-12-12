package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AboveTheSky(val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.abovethesky

    override fun name(): String = "Above The Sky"

    override fun rawFile(): Int = R.raw.abovethesky
}