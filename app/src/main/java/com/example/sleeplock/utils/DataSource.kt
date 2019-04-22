package com.example.sleeplock.utils

import com.example.sleeplock.R

const val ACTION_PLAY = "play"
const val ACTION_PAUSE = "pause"
const val ACTION_RESET = "reset"
const val ACTION_FORCE_STOP = "force stop"
const val CHANNEL_ID = "MyService"
const val CURRENT_TIME = "current time"
const val SHARED_PREFS = "shared preferences"
const val IS_SERVICE_RUNNING = "is service running boolean"
const val MILLIS = "millis"


val ITEM_PIC = listOf(
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

val ITEM_TEXT = listOf(
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