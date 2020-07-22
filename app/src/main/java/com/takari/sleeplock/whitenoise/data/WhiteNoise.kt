package com.takari.sleeplock.whitenoise.data

import android.os.Parcelable

/**
 * Defines the type of all white noise options shown to the user.
 */
interface WhiteNoise : Parcelable {

    fun image(): Int

    fun name(): String

    fun description(): String

    fun sound(): Int

    //keep this, may still need this later on
    fun colorHexCode(): String
}
