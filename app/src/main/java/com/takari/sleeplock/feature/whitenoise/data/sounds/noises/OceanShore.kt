package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.R
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OceanShore(val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.oceanshore

    override fun name(): String = "Ocean Shore"

    override fun rawFile(): Int = R.raw.oceanshore
}
