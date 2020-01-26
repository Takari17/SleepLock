package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MorningBliss(val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.morningbliss

    override fun name(): String = "Morning Bliss"

    override fun rawFile(): Int = R.raw.morningbliss
}
