package com.example.sleeplock.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.sleeplock.data.local.SharedPrefs
import com.example.sleeplock.data.service.MainService
import com.example.sleeplock.utils.INDEX
import com.example.sleeplock.utils.IntentAction
import com.example.sleeplock.utils.MILLIS
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/*
 *Communicates with, and returns data from, MainService.kt.
 */
@Singleton
class Repository @Inject constructor(
    private val context: Context,
    val sharedPrefs: SharedPrefs
) {

    private val serviceIntent: Intent = Intent(context, MainService::class.java)

    private val compositeDisposable = CompositeDisposable()

    private var mainService: MainService? = null

    var wasTimerStarted = false

    private val currentTime = MediatorLiveData<Long>()
    private val isTimerRunning = MediatorLiveData<Boolean>()
    val isTimerCompleted = PublishRelay.create<Boolean>()


    fun getCurrentTime(): LiveData<Long> = currentTime

    fun getIsTimerRunning(): LiveData<Boolean> = isTimerRunning

    // Service will start and play the sound and timer.
    fun startSoundAndTimer(millis: Long, index: Int) {
        serviceIntent.apply {
            action = IntentAction.PLAY.name
            putExtra(MILLIS, millis)
            putExtra(INDEX, index)
        }

        context.apply {
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, 0)
        }
    }

    fun pauseSoundAndTimer() = mainService?.pauseSoundAndTimer()

    fun resumeSoundAndTimer() = mainService?.resumeSoundAndTimer()

    fun resetSoundAndTimer() = mainService?.resetSoundAndTimer()

    fun bindToServiceIfRunning() {
        if (MainService.isRunning)
            context.bindService(serviceIntent, serviceConnection, 0)
    }

    fun unbindFromServiceIfRunning() {
        if (MainService.isRunning)
            context.unbindService(serviceConnection)
    }


    private fun addServiceLiveDataSources(service: MainService) {

        currentTime.addSource(service.getCurrentTime()) { millis ->
            currentTime.value = millis
        }

        isTimerRunning.addSource(service.getIsTimerRunning()) { isRunning ->
            isTimerRunning.value = isRunning
        }
    }

    fun removeServiceLiveDataSources() = mainService?.let { service ->

        currentTime.removeSource(service.getCurrentTime())

        isTimerRunning.removeSource(service.getIsTimerRunning())
    }

    // Cleared in the MainViewModel's onClear() callback.
    fun clearDisposables() = compositeDisposable.clear()


    //When bound the Repository observes the services Live Data objects and subscribes to all Observables.

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            (service as MainService.LocalBinder).getService().also { serviceReference ->

                mainService = serviceReference

                addServiceLiveDataSources(serviceReference)

                compositeDisposable += serviceReference.isTimerCompleted
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                        onNext = { isCompleted -> isTimerCompleted.accept(isCompleted) },
                        onError = { Log.d("zwi", "Error observing isTimerCompleted in Repository: $it") }
                    )

                compositeDisposable += serviceReference.getWasTimerStarted()
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                        onNext = { wasStarted -> wasTimerStarted = wasStarted },
                        onError = { Log.d("zwi", "Error observing wasTimerStarted in Repository: $it") }
                    )
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}