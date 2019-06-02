package com.example.sleeplock.utils

import com.example.sleeplock.R

/*
Holds constants used throughout the code base.
 */

const val ACTION_PLAY = "play"
const val ACTION_PAUSE = "pause"
const val ACTION_RESET = "reset"
const val CHANNEL_ID = "MainService"
const val INDEX = "index"
const val MILLIS = "millis"
const val NOTIFICATION_ID = 1001
const val BUTTON_ENABLED = "button enabled"
const val BUTTON_COLOR = "button color"
const val BUTTON_TEXT = "button text"
const val CARD_VIEW_IMAGE = "card view image"
const val CARD_VIEW_TEXT = "card view text"
const val IS_TIME_CHOSEN = "is time chosen"
const val IS_SOUND_CHOSEN = "is sound chosen"


val ITEM_PIC = listOf(
    R.drawable.rain,
    R.drawable.oceanshore,
    R.drawable.underwater,
    R.drawable.morningnature,
    R.drawable.birds,
    R.drawable.night,
    R.drawable.fan,
    R.drawable.heli,
    R.drawable.fire,
    R.drawable.boiling,
    R.drawable.plane,
    R.drawable.waterstream
)

val ITEM_TEXT = listOf(
    "Rain",
    "OceanShore",
    "UnderWater",
    "Morning Bliss",
    "Chirping Birds",
    "Peaceful Night",
    "Fan",
    "Helicopter",
    "Camp Fire",
    "Boiling Water",
    "Above the Sky",
    "Water Stream"
)

val WHITE_NOISE = listOf(
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

val timeOptions = arrayOf(
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