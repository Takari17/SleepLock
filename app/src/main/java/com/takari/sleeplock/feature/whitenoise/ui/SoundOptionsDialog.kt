package com.takari.sleeplock.feature.whitenoise.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.common.logD
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import com.takari.sleeplock.feature.whitenoise.data.sounds.noises.*
import io.reactivex.Observable


class SoundOptionsDialog : AppCompatDialogFragment() {

    private val clickedWhiteNoiseData = PublishRelay.create<WhiteNoise>()
    private val whiteNoiseList = listOf(
        Rain(),
        OceanShore(),
        DeepSea(),
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

    private val whiteNoiseAdapter = WhiteNoiseAdapter(whiteNoiseList) { clickedSoundData ->
            clickedWhiteNoiseData.accept(clickedSoundData)
            dismiss()
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(activity).apply {
            val view = activity!!.layoutInflater.inflate(R.layout.sound_option_layout, null)
            setView(view)

            //Kotlin synthetics don't work here for whatever reason
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                adapter = whiteNoiseAdapter
            }

        }.create()

    fun getClickedWhiteNoiseData(): Observable<WhiteNoise> = clickedWhiteNoiseData
}
