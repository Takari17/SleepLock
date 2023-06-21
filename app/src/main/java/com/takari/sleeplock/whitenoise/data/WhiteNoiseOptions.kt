package com.takari.sleeplock.whitenoise.data

import com.takari.sleeplock.whitenoise.data.sounds.AirPlane
import com.takari.sleeplock.whitenoise.data.sounds.CampFire
import com.takari.sleeplock.whitenoise.data.sounds.DeepSea
import com.takari.sleeplock.whitenoise.data.sounds.Helicopter
import com.takari.sleeplock.whitenoise.data.sounds.Nightfall
import com.takari.sleeplock.whitenoise.data.sounds.OceanShore
import com.takari.sleeplock.whitenoise.data.sounds.Rain
import com.takari.sleeplock.whitenoise.data.sounds.Sunrise

/**
 * Defines the white noises and viewing order of them used throughout this feature. The data in
 * this module is static, so there's no need for a data base.
 *
 * To add a new sound just create a new implementation of WhiteNoise and add it to this
 * list in the order you want.
 */

val allWhiteNoises = listOf(
    Rain(),
    OceanShore(),
    DeepSea(),
    Sunrise(),
    Nightfall(),
    Helicopter(),
    CampFire(),
    AirPlane()
)
