package com.example.sleeplock.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.sleeplock.data.service.MainService
import com.example.sleeplock.utils.ACTION_PLAY
import com.example.sleeplock.utils.INDEX
import com.example.sleeplock.utils.MILLIS
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Repository @Inject constructor(
    private val context: Context
) {

    private val serviceIntent: Intent = Intent(context, MainService::class.java)

    private var mainService: MainService? = null

    var isServiceRunning = false

    // Sources = services Live Data objects
    private val currentTime = MediatorLiveData<Long>()
    private val isTimerRunning = MediatorLiveData<Boolean>()
    private var isTimerPaused = MediatorLiveData<Boolean>()
    private var isTimerCompleted = MediatorLiveData<Boolean>()
    private val isBound = MediatorLiveData<Boolean>()
    private val serviceStatus = MediatorLiveData<Boolean>()


    init { // remove observers if the service unbinds
        isBound.observeForever { isBinded ->
            if (!isBinded) removeServiceLiveDataSources()// todo thinkin we do this onStop instead of through LD
        }
    }

    fun getCurrentTime(): LiveData<Long> = currentTime

    fun getIsTimerRunning(): LiveData<Boolean> = isTimerRunning

    fun getIsTimerPaused(): LiveData<Boolean> = isTimerPaused

    fun getIsTimerCompleted(): LiveData<Boolean> = isTimerCompleted


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

    fun pauseSoundAndTimer() = mainService?.pauseSoundAndTimer()

    fun resumeSoundAndTimer() = mainService?.startSoundAndTimer()

    fun resetSoundAndTimer() = mainService?.resetSoundAndTimer()

    fun bindToServiceIfRunning() {
        if (isServiceRunning) context.bindService(serviceIntent, serviceConnection, 0)
    }

    private fun addServiceLiveDataSources(service: MainService) {

        currentTime.addSource(service.getCurrentTime()) { millis ->
            currentTime.value = millis
        }

        isTimerRunning.addSource(service.getIsTimerRunning()) { isRunning ->
            isTimerRunning.value = isRunning
        }

        isTimerPaused.addSource(service.getIsTimerPaused()) { isPaused ->
            isTimerPaused.value = isPaused
        }

        isTimerCompleted.addSource(service.getIsTimerCompleted()) { isCompleted ->
            isTimerCompleted.value = isCompleted
        }

        isBound.addSource(service.getIsBound()) { isServiceBound ->
            isBound.value = isServiceBound
        }

        serviceStatus.addSource(service.getIsServiceRunning()) { isRunning ->
            isServiceRunning = isRunning

        }
    }

    private fun removeServiceLiveDataSources() =
        mainService?.let { service ->
            currentTime.removeSource(service.getCurrentTime())

            isTimerRunning.removeSource(service.getIsTimerRunning())

            isTimerPaused.removeSource(service.getIsTimerPaused())

            isTimerCompleted.removeSource(service.getIsTimerCompleted())

            isBound.removeSource(service.getIsBound())

            serviceStatus.removeSource(service.getIsServiceRunning())
        }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val serviceReference = (service as MainService.LocalBinder).getService()

            addServiceLiveDataSources(serviceReference)

            mainService = serviceReference
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}