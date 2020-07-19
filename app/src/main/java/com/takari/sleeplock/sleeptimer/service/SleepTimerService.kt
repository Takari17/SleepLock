package com.takari.sleeplock.sleeptimer.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.takari.sleeplock.App
import com.takari.sleeplock.R
import com.takari.sleeplock.main.MainActivity
import com.takari.sleeplock.shared.TimerFlow
import com.takari.sleeplock.shared.to24HourFormat
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion


class SleepTimerService : Service() {

    companion object {
        fun isRunning() = isServiceRunning
        const val MILLIS = "millis"
        const val START = "start"
        const val PAUSE = "pause"
        const val RESUME = "resume"
        const val CANCEL = "reset"
    }

    private lateinit var timerFlow: TimerFlow
    private val deviceSleeper = App.applicationComponent.deviceSleeper

    // the view observes these when it binds
    private val _elapseTime: MutableLiveData<String> = MutableLiveData("00:00")
    val elapseTime: LiveData<String> = _elapseTime

    private val _timerIsRunning: MutableLiveData<Boolean> = MutableLiveData(true)
    val timerIsRunning: LiveData<Boolean> = _timerIsRunning

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    var onServiceCanceled: (Unit) -> Unit = {}

    private var id = 3356
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }


    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        notificationBuilder = NotificationCompat.Builder(this, App.CHANNEL_ID)
    }

    inner class LocalBinder : Binder() {
        fun getService(): SleepTimerService = this@SleepTimerService
    }

    override fun onBind(intent: Intent): IBinder? = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {

            START -> {

                val millis: Long = intent.getLongExtra(MILLIS, 0)

                timerFlow = TimerFlow(millis) { isRunning ->
                    serviceScope.launch {
                        withContext(Dispatchers.Main) {
                            _timerIsRunning.value = isRunning
                            updateNotificationAction(isRunning, _elapseTime.value ?: "00:00")
                        }
                    }
                }

                observeTimerFlow(timerFlow)

                startForeground(id, getInitialTimerNotification())
            }

            PAUSE -> pauseTimer()
            RESUME -> resumeTimer()
            CANCEL -> destroyService()
        }

        return START_STICKY
    }


    private fun sleepTheDevice() = serviceScope.launch {
        deviceSleeper.slowlySleepDeviceFlow
            .onCompletion { destroyService() }
            .flowOn(Dispatchers.IO)
            .collect()
    }

    fun destroyService() {
        stopSelf()
        stopForeground(true)

    }

    /*
    If the timer finishes itself, it sleeps the device and on complete destroys the service. If the
    user cancels the service manually, destroy service is just called directly
 */
    override fun onDestroy() {
        super.onDestroy()
        _elapseTime.value = "00:00"
        isServiceRunning = false
        timerFlow.reset()
        onServiceCanceled(Unit)
        notificationManager.cancelAll()
        serviceScope.cancel()

    }

    private fun observeTimerFlow(timerFlow: TimerFlow) = serviceScope.launch {
        timerFlow.get
            .flowOn(Dispatchers.IO)
            .collect { millis ->
                _elapseTime.value = millis.to24HourFormat()
                updateNotificationText(millis.to24HourFormat())

                if (millis == 0L) {
                    //timer finished on it's own and wasn't manually killed
                    sleepTheDevice()
                    _elapseTime.value = "Sleeping Device"
                    updateNotificationText("Sleeping Device")
                }
            }
    }

    private fun getInitialTimerNotification(): Notification = notificationBuilder.apply {
        setSmallIcon(R.drawable.alarm_icon)
        addAction(R.drawable.pause, "Pause", createBroadcastIntent(WhiteNoiseService.PAUSE))
        addAction(R.drawable.reset, "Cancel", createBroadcastIntent(WhiteNoiseService.RESET))
        setSubText("Sound Options")
        setContentTitle("Sleep Lock")
        setContentText(0L.to24HourFormat())
        color = ContextCompat.getColor(this@SleepTimerService, R.color.colorAccent)
        setColorized(true)
        setContentIntent(activityIntent)
    }.build()


    private fun updateNotificationText(newText: String) {
        notificationBuilder.setContentText(newText).build()
        notificationManager.notify(id, notificationBuilder.build())
    }

    //Gotta create a new builder since you can't replace notification actions :/
    private fun updateNotificationAction(timerIsRunning: Boolean, elapseTime: String) {

        notificationBuilder = NotificationCompat.Builder(this, App.CHANNEL_ID)

        if (timerIsRunning)
            notificationBuilder.addAction(
                R.drawable.pause, "Pause", createBroadcastIntent(WhiteNoiseService.PAUSE)
            )
        else notificationBuilder.addAction(
            R.drawable.play, "Resume", createBroadcastIntent(WhiteNoiseService.RESUME)
        )

        notificationBuilder.apply {
            setSmallIcon(R.drawable.alarm_icon)
            addAction(R.drawable.reset, "Cancel", createBroadcastIntent(WhiteNoiseService.RESET))
            setSubText("Sound Options")
            setContentTitle("SleepLock")
            setContentText(elapseTime)
            setContentIntent(activityIntent)
            color = ContextCompat.getColor(this@SleepTimerService, R.color.colorAccent)
            setColorized(true)
        }

        notificationManager.notify(id, notificationBuilder.build())
    }


    private val activityIntent by lazy {
        val intent = Intent(this, MainActivity::class.java)
        PendingIntent.getActivity(this, 1, intent, 0)
    }

    private fun createBroadcastIntent(action: String): PendingIntent {
        val intent = Intent(this, SleepTimerServiceReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(this, 0, intent, 0)
    }

    fun pauseTimer() {
        timerFlow.pause()
    }

    fun resumeTimer() {
        timerFlow.resume()
    }
}

private var isServiceRunning = false