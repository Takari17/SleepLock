package com.example.sleeplock.data

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.sleeplock.data.service.MyService
import com.example.sleeplock.data.service.isServiceRunning
import com.example.sleeplock.utils.ACTION_PLAY
import com.example.sleeplock.utils.INDEX
import com.example.sleeplock.utils.MILLIS


class Repository(private val application: Application) {

    private val serviceIntent: Intent = Intent(application, MyService::class.java)

    lateinit var myService: MyService

    // Sources = services Live Data objects
    private val currentTime = MediatorLiveData<Long>()
    private val timerStarted = MediatorLiveData<Boolean>()
    private val timerPaused = MediatorLiveData<Boolean>()
    private val timerCompleted = MediatorLiveData<Boolean>()
    private val isBound = MediatorLiveData<Boolean>()


    fun getCurrentTime(): LiveData<Long> = currentTime
    fun getTimerStarted(): LiveData<Boolean> = timerStarted
    fun getTimerPaused(): LiveData<Boolean> = timerPaused
    fun getTimerCompleted(): LiveData<Boolean> = timerCompleted


    init { // remove observers if the service unbinds
        isBound.observeForever { isBinded ->
            if (!isBinded) removeLiveDataSources()
        }
    }


    fun startSoundAndTimer(millis: Long, index: Int) {
        // service will start and play the sound and timer
        serviceIntent.apply {
            action = ACTION_PLAY
            putExtra(MILLIS, millis)
            putExtra(INDEX, index)
        }
        application.startService(serviceIntent)
        isServiceRunning = true
        bindToService()
    }

    fun pauseSoundAndTimer() = myService.pauseSoundAndTimer()

    fun resumeSoundAndTimer() = myService.startSoundAndTimer()

    fun resetSoundAndTimer() = myService.resetTimer()

    fun bindToService() {
        if (isServiceRunning) application.bindService(serviceIntent, serviceConnection, 0)
    }

    private fun addLiveDataSources() {
        currentTime.addSource(myService.getCurrentTime()) { millis -> currentTime.value = millis }
        timerStarted.addSource(myService.getTimerStarted()) { timerStarted.value = it }
        timerPaused.addSource(myService.getTimerPaused()) { timerPaused.value = it }
        timerCompleted.addSource(myService.getTimerCompleted()) { timerCompleted.value = it }
        isBound.addSource(myService.getIsBound()) { isBound.value = it }
    }

    private fun removeLiveDataSources() {
        currentTime.removeSource(myService.getCurrentTime())
        timerStarted.removeSource(myService.getTimerStarted())
        timerPaused.removeSource(myService.getTimerPaused())
        timerCompleted.removeSource(myService.getTimerCompleted())
        isBound.removeSource(myService.getIsBound())
    }

    //todo do we unbind from this service?
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val serviceReference = (service as MyService.LocalBinder).getService()

            myService = serviceReference
            addLiveDataSources()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}