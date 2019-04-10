package com.example.sleeplock

import android.graphics.drawable.Drawable

class DataSource {

    val ITEM_PIC = listOf<Int>(
        R.drawable.rain,
        R.drawable.oceanshore,
        R.drawable.underwater,
        R.drawable.morningnature,
        R.drawable.birds,
        R.drawable.naturenight,
        R.drawable.fan,
        R.drawable.heli,
        R.drawable.fire,
        R.drawable.boiling,
        R.drawable.plane,
        R.drawable.waterstream
    )

    val ITEM_TEXT = listOf<String>(
        "Rain",
        "OceanShore",
        "UnderWater",
        "Morning",
        "Chirping Birds",
        "Peaceful Night",
        "Fan",
        "Helicopter",
        "Camp Fire",
        "Boiling Water",
        "Plane",
        "Water Stream"
    )

    val WHITE_NOISE = listOf<Int>(
        R.raw.rain,
        R.raw.oceanshore,
        R.raw.underwater,
        R.raw.morningnature,
        R.raw.naturebirds,
        R.raw.nightnoise,
        R.raw.fan,
        R.raw.heli,
        R.raw.fire,
        R.raw.boiling,
        R.raw.plane,
        R.raw.waterstream
    )

    val timeOptions =  arrayOf(
        "Select Custom Time",
        "10 min",
        "20 min",
        "30 min",
        "40 min",
        "50 min",
        "60 min",
        "70 min",
        "80 min",
        "90 min"

    )


}
