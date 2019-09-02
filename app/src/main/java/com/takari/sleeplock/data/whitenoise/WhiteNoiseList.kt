package com.takari.sleeplock.data.whitenoise

import android.content.Context
import com.takari.sleeplock.R
import com.takari.sleeplock.utils.getResourceString
import javax.inject.Inject

/*
todo This wont scale well, that's how you know it's bad design. A Data Base would be more appropriate if
 we decide to add more stuff to this app, but for now it's fine.
 */
class WhiteNoiseList @Inject constructor() {

    private enum class SoundData(
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

    }

    fun getAllImages(): List<Int> {
        val images = mutableListOf<Int>()

        SoundData.values().forEach { itemData ->
            images.add(itemData.imageReference)
        }
        return images.toList()
    }

    fun getAllNoises(): List<Int> {
        val noises = mutableListOf<Int>()

        SoundData.values().forEach { itemData ->
            noises.add(itemData.soundReference)
        }
        return noises.toList()
    }

    fun getAllNames(context: Context): List<String> {
        val names = mutableListOf<String>()

        SoundData.values().forEach { itemData ->
            names.add(getResourceString(context,itemData.textReference))
        }
        return names.toList()
    }
}