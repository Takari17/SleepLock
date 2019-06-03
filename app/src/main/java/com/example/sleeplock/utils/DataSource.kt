package com.example.sleeplock.utils

import android.content.Context
import com.example.sleeplock.R

/*
Holds constants used throughout the code base.
 */

const val CHANNEL_ID = "MainService"
const val NOTIFICATION_ID = 1001
const val INDEX = "index"
const val MILLIS = "millis"
const val BUTTON_ENABLED = "button enabled"
const val BUTTON_COLOR = "button color"
const val BUTTON_TEXT = "button text"
const val CARD_VIEW_IMAGE = "card view image"
const val CARD_VIEW_TEXT = "card view text"
const val IS_TIME_CHOSEN = "is time chosen"
const val IS_SOUND_CHOSEN = "is sound chosen"

enum class IntentAction {
    PLAY, PAUSE, RESET
    }

enum class ItemData(
    val imageReference: Int,
    val textReference: Int,
    val soundReference: Int
) {
    RAIN(
        R.drawable.rain,
        R.string.rain,
        R.raw.rain
    ),
    OCEAN_SHORE(
        R.drawable.oceanshore,
        R.string.oceanShore,
        R.raw.oceanshore
    ),
    UNDER_WATER(
        R.drawable.underwater,
        R.string.underwater,
        R.raw.underwater
    ),
    MORNING_BLISS(
        R.drawable.morningbliss,
        R.string.morningBliss,
        R.raw.morningbliss
    ),
    CHIRPING_BIRDS(
        R.drawable.chirpingbirds,
        R.string.chirpingBirds,
        R.raw.chirpingbirds
    ),
    PEACEFUL_NIGHT(
        R.drawable.peacefulnight,
        R.string.peacefulNight,
        R.raw.peacefulnight
    ),
    FAN(
        R.drawable.fan,
        R.string.fan,
        R.raw.fan
    ),
    HELICOPTER(
        R.drawable.helicopter,
        R.string.helicopter,
        R.raw.helicopter
    ),
    CAMP_FIRE(
        R.drawable.campfire,
        R.string.campFire,
        R.raw.campfire
    ),
    BOILING_WATER(
        R.drawable.boilingwater,
        R.string.boilingWater,
        R.raw.boilingwater
    ),
    ABOVE_THE_SKY(
        R.drawable.abovethesky,
        R.string.aboveTheSky,
        R.raw.abovethesky
    ),
    WATER_STREAM(
        R.drawable.waterstream,
        R.string.waterStream,
        R.raw.waterstream
    );

    companion object {
        fun getAllImageReferences(): List<Int> {
            val imageReference = mutableListOf<Int>()

            values().forEach { itemData ->
                imageReference.add(itemData.imageReference)
            }
            return imageReference.toList()
        }

        fun getAllSoundReferences(): List<Int> {
            val soundReference = mutableListOf<Int>()

            values().forEach { itemData ->
                soundReference.add(itemData.soundReference)
            }
            return soundReference.toList()
        }

        fun getAllText(context: Context): List<String> {
            val textList = mutableListOf<String>()

            values().forEach { itemData ->
                textList.add(getResourceString(context, itemData.textReference))
            }
            return textList.toList()
        }
    }
}

enum class TimeOptions(val stringReference: Int) {
    SELECT_A_TIME(R.string.customTime),
    TEN(R.string.tenMin),
    TWENTY(R.string.twentyMin),
    THIRTY(R.string.thirtyMin),
    FORTY(R.string.fortyMin),
    FIFTY(R.string.fiftyMin),
    SIXTY(R.string.sixtyMin),
    SEVENTY(R.string.seventyMin),
    EIGHTY(R.string.eightyMin),
    NINETY(R.string.ninetyMin);

    companion object {
        fun getTimeOptions(context: Context): Array<String> {
            val imageReference = arrayListOf<String>()

            values().forEach { timeOption ->
                imageReference.add(getResourceString(context, timeOption.stringReference))
            }
            return imageReference.toTypedArray()
        }
    }
}