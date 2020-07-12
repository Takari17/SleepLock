package com.takari.sleeplock.whitenoise.data.sounds

import com.takari.sleeplock.R
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CampFire(private val placeHolder: String = "") :
    WhiteNoise {

    override fun image(): Int = R.drawable.campfire

    override fun name(): String = "Camp Fire"

    override fun description(): String =
        "Listen to the crackling flames of a campfire"

    override fun sound(): Int = R.raw.campfire

    override fun colorHexCode(): String = "#d32f2f"
}