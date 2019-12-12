package com.takari.sleeplock.feature.whitenoise.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.takari.sleeplock.feature.common.Timer
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import com.takari.sleeplock.feature.whitenoise.ui.WhiteNoiseViewState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhiteNoiseRepository @Inject constructor(private val sharedPrefs: SharedPreferences) {

    val currentTime = BehaviorRelay.create<Long>()
    var timerAction = BehaviorRelay.create<Timer.Action>()
    val completed = PublishRelay.create<Unit>()
    var hasTimerStarted = false
    var isTimerRunning = false


    private val gson: Gson = GsonBuilder().registerTypeAdapter(WhiteNoise::class.java, InterfaceAdapter())
            .create()

    fun saveState(state: WhiteNoiseViewState) {
        val jsonState = gson.toJson(state)
        sharedPrefs.edit { putString("State", jsonState) }
    }

    fun restoreState(): WhiteNoiseViewState {
        val jsonState = sharedPrefs.getString("State", "null")!!
        return gson.fromJson(jsonState, WhiteNoiseViewState::class.java)
    }

    fun reset() {
        sharedPrefs.edit { clear() }
        completed.accept(Unit)
    }
}
