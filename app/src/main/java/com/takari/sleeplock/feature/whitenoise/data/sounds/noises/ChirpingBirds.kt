package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChirpingBirds(val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.chirpingbirds

    override fun name(): String = "Chirping Birds"

    override fun rawFile(): Int = R.raw.chirpingbirds
}