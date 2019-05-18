package com.example.sleeplock.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.sleeplock.data.service.MyService
import com.example.sleeplock.utils.ACTION_PLAY
import com.example.sleeplock.utils.INDEX
import com.example.sleeplock.utils.MILLIS
import javax.inject.Inject
import javax.inject.Singleton


//todo ensure the service is not started unless index and millis are not null
@Singleton
class Repository @Inject constructor(
    private val context: Context
) {

    private val serviceIntent: Intent = Intent(context, MyService::class.java)

    private var myService: MyService? = null

    var isServiceRunning = false

    // Sources = services Live Data objects
    private val currentTime = MediatorLiveData<Long>()
    private val timerStarted = MediatorLiveData<Boolean>()
    private val timerPaused = MediatorLiveData<Boolean>()
    private val timerCompleted = MediatorLiveData<Boolean>()
    private val isBound = MediatorLiveData<Boolean>()
    private val serviceStatus = MediatorLiveData<Boolean>()


    fun getCurrentTime(): LiveData<Long> = currentTime
    fun getTimerStarted(): LiveData<Boolean> = timerStarted
    fun getTimerPaused(): LiveData<Boolean> = timerPaused
    fun getTimerCompleted(): LiveData<Boolean> = timerCompleted


    init { // remove observers if the service unbinds
        isBound.observeForever { isBinded ->
            if (!isBinded) removeLiveDataSources()// todo thinkin we do this onStop instead of through LD
        }
    }


    fun startSoundAndTimer(millis: Long, index: Int) {
        // service will start and play the sound and timer
        serviceIntent.apply {
            action = ACTION_PLAY
            putExtra(MILLIS, millis)
            putExtra(INDEX, index)
        }
        context.startService(serviceIntent)
        bindToServiceIfRunning()
    }

    fun pauseSoundAndTimer() = myService?.pauseSoundAndTimer()

    fun resumeSoundAndTimer() = myService?.startSoundAndTimer()


    fun resetSoundAndTimer() = myService?.resetSoundAndTimer()

    fun bindToServiceIfRunning() {
        if (isServiceRunning) context.bindService(serviceIntent, serviceConnection, 0)
    }

    private fun addLiveDataSources() {
        //todo remove these it's
        myService?.let { service ->
            currentTime.addSource(service.getCurrentTime()) { millis -> currentTime.value = millis }
            timerStarted.addSource(service.getTimerStarted()) { timerStarted.value = it }
            timerPaused.addSource(service.getTimerPaused()) { timerPaused.value = it }
            timerCompleted.addSource(service.getTimerCompleted()) { timerCompleted.value = it }
            isBound.addSource(service.getIsBound()) { isBound.value = it }
            serviceStatus.addSource(service.getIsServiceRunning()) {isRunning -> isServiceRunning = isRunning}
        }
    }

    private fun removeLiveDataSources() {
        myService?.let { service ->
            currentTime.removeSource(service.getCurrentTime())
            timerStarted.removeSource(service.getTimerStarted())
            timerPaused.removeSource(service.getTimerPaused())
            timerCompleted.removeSource(service.getTimerCompleted())
            isBound.removeSource(service.getIsBound())
            serviceStatus.removeSource(service.getIsServiceRunning())
        }
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val serviceReference = (service as MyService.LocalBinder).getService()

            myService = serviceReference
            addLiveDataSources()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}