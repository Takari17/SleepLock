package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
class CampFire(val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.campfire

    override fun name(): String = "Camp Fire"

    override fun rawFile(): Int = R.raw.campfire
}