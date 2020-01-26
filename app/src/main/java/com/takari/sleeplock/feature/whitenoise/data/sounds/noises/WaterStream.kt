package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WaterStream(val placeHolder: String = "") : WhiteNoise {

    override fun image(): Int = R.drawable.waterstream

    override fun name(): String = "Water Stream"

    override fun rawFile(): Int = R.raw.waterstream
}
