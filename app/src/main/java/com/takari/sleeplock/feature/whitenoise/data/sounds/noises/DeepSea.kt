package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeepSea(val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.deepsea

    override fun name(): String = "Deep Sea"

    override fun rawFile(): Int = R.raw.deepsea
}
