package com.takari.sleeplock.whitenoise.data

import android.os.Parcelable


interface WhiteNoise : Parcelable {

    fun image(): Int

    fun name(): String

    fun description(): String

    fun sound(): Int

    fun colorHexCode(): String
}
