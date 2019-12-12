package com.takari.sleeplock.feature.sleeptimer

import android.content.SharedPreferences
import androidx.core.content.edit
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.takari.sleeplock.feature.common.Timer
import com.takari.sleeplock.feature.whitenoise.service.WhiteNoiseService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepTimerRepository @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    val currentTime = BehaviorRelay.create<Long>()
    var timerAction = BehaviorRelay.create<Timer.Action>()
    val completed = PublishRelay.create<Unit>()
    var hasTimerStarted = false
    var isTimerRunning = false


    fun hideQuestionView(): Boolean =
        sharedPrefs.getBoolean("question view", false)


    fun saveHideQuestionView(bool: Boolean){
        sharedPrefs.edit { putBoolean("question view", bool) }
    }

    fun whiteNoiseServiceRunning() = WhiteNoiseService.isRunning()
}
