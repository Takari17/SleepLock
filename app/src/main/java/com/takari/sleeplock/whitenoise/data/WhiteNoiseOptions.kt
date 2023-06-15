package com.takari.sleeplock.whitenoise.data

import com.takari.sleeplock.whitenoise.data.sounds.*

/**
 * Defines the white noises and viewing order of them used throughout this module. The data in
 * this module is static, so there's no need for a data base.
 *
 * To add a new sound just create a new Impl of WhiteNoise and add it to this list in the
 * order you want.
 */
object WhiteNoiseOptions {
    val get = listOf(
        Rain(),
        OceanShore(),
//        DeepSea(),
        Sunrise(),
        Nightfall(),
        Helicopter(),
        CampFire(),
        AirPlane()
    )

    fun getIndexOfItemInList(whiteNoise: WhiteNoise): Int? {
        var indexOfItem: Int? = null

        for ((index, i) in get.withIndex()) {
            if (i == whiteNoise) {
                indexOfItem = index
            }
        }

        return indexOfItem
    }
}