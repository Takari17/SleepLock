package com.takari.sleeplock.feature.whitenoise.data.sounds

import android.os.Parcelable

/*
We're not gonna use a Room data base because our sounds are static. A db would be
the definition of overkill.
 */
interface WhiteNoise : Parcelable {

    fun image(): Int

    fun name(): String

    fun rawFile(): Int
}



