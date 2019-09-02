package com.takari.sleeplock.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.takari.sleeplock.data.SleepTimerService.Companion.TIME
import com.takari.sleeplock.data.SleepTimerService.Companion.WHITE_NOISE
import com.takari.sleeplock.data.local.SharedPrefs
import com.takari.sleeplock.data.whitenoise.WhiteNoiseList
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Controls and observes SleepTimerService as well as sets/gets data from shared preferences.
 */
@Singleton
class Repository @Inject constructor(
    private val context: Context,
    private val sharedPrefs: SharedPrefs,
    private var sleepTimerService: SleepTimerService?,
    val whiteNoiseList: WhiteNoiseList
) {

    private val serviceIntent: Intent = Intent(context, SleepTimerService::class.java)

    // Used for chaining the observables from the Timer Service
    val currentTime = BehaviorRelay.create<Long>()

    val isTimerRunning = BehaviorRelay.create<Boolean>()

    val hasTimerStarted = BehaviorRelay.create<Boolean>()

    val timerCompleted = BehaviorRelay.create<Unit>()


    /**
     * Gets the value of isTimerRunning and returns it. If it's null, instead of returning
     * null it just returns false.
     */
    fun isTimerRunningBoolean(): Boolean =
        if (isTimerRunning.value == null) false
        else isTimerRunning.value!!

    /**
     * Gets the value of hasTimerStarted and returns it. If it's null, instead of returning
     * null it just returns false.
     */
    fun hasTimerStartedBoolean(): Boolean =
        if (hasTimerStarted.value == null) false
        else hasTimerStarted.value!!

    /**
     * Saves a value in shared preferences if it's not null.
     */
    fun saveValueIfNonNull(key: String, value: Any?) {
        sharedPrefs.saveValueIfNonNull(key, value)
    }

    fun getInt(key: String, defaultValue: Int): Int =
        sharedPrefs.getInt(key, defaultValue)

    fun getString(key: String, defaultValue: String): String =
        sharedPrefs.getString(key, defaultValue)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPrefs.getBoolean(key, defaultValue)

    fun resetSharedPrefsData() {
        sharedPrefs.resetAllData()
    }

    /**
     * Starts SleepTimerService and plays the white noise and timer.
     */
    fun startSoundAndTimer(millis: Long, whiteNoise: Int) {
        serviceIntent.apply {
            action = SleepTimerService.IntentAction.START.name
            putExtra(TIME, millis)
            putExtra(WHITE_NOISE, whiteNoise)
        }

        context.apply {
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, 0)
        }
    }

    fun pauseSoundAndTimer() = sleepTimerService?.pauseSoundAndTimer()

    fun resumeSoundAndTimer() = sleepTimerService?.resumeSoundAndTimer()

    fun resetSoundAndTimer() = sleepTimerService?.resetSoundAndTimer()


    /**
    Subscribes to the exposed timer callback observables from Timer Service.
     */
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val serviceReference = (service as SleepTimerService.LocalBinder).getService()

            sleepTimerService = serviceReference

            // These should already be on a background thread, also no need to dispose observables in model layer.
            serviceReference.getCurrentTime()
                .subscribeBy(
                    onNext = { time -> currentTime.accept(time) },
                    onError = { Log.d("zwi", "Error observing currentTime in Repository: $it") }
                )

            serviceReference.getIsTimerRunning()
                .subscribeBy(
                    onNext = { isRunning -> isTimerRunning.accept(isRunning) },
                    onError = { Log.d("zwi", "Error observing isRunning in Repository: $it") }
                )

            serviceReference.getHasTimerStarted()
                .subscribeBy(
                    onNext = { hasStarted -> hasTimerStarted.accept(hasStarted) },
                    onError = { Log.d("zwi", "Error observing hasStarted in Repository: $it") }
                )

            serviceReference.getTimerCompleted()
                .subscribeBy(
                    onNext = {
                        timerCompleted.accept(Unit)
                        resetSharedPrefsData()
                    },
                    onError = { Log.d("zwi", "Error observing completed in Repository: $it") }
                )

        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}