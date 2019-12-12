package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnderWater(val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.underwater

    override fun name(): String = "Under Water"

    override fun rawFile(): Int = R.raw.underwater
}
