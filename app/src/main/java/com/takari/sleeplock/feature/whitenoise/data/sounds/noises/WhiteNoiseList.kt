package com.takari.sleeplock.feature.whitenoise.data.sounds.noises

import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise

class WhiteNoiseList {

    // dictates the order of the sounds shown in the recycler view
   fun getAllWhiteNoises(): List<WhiteNoise> = listOf(
        Rain(),
        OceanShore(),
        UnderWater(),
        MorningBliss(),
        ChirpingBirds(),
        PeacefulNight(),
        Fan(),
        Helicopter(),
        CampFire(),
        BoilingWater(),
        AboveTheSky(),
        WaterStream()
    )
}