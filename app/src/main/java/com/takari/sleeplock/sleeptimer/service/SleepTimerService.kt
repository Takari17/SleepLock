package com.takari.sleeplock.sleeptimer.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.takari.sleeplock.MainActivity
import com.takari.sleeplock.R
import com.takari.sleeplock.di.App
import com.takari.sleeplock.shared.TimerFlow
import com.takari.sleeplock.shared.log
import com.takari.sleeplock.shared.to24HourFormat
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

private var isServiceRunning = false
private var isTimerRunning = false

@AndroidEntryPoint
class SleepTimerService : Service() {

    companion object {
        const val MILLIS = "millis"
        const val START = "start"
        const val PAUSE = "pause"
        const val RESUME = "resume"
        const val RESET = "reset"
        const val NOTIFICATION_ID = 8936457

        fun isRunning() = isServiceRunning
        fun timerIsRunning() = isTimerRunning
    }

    lateinit var timerFlow: TimerFlow

    @Inject
    lateinit var deviceSleeper: DeviceSleeper

    private val serviceScope = CoroutineScope(Dispatchers.Main)

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    private val notificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(this, App.CHANNEL_ID)
    }


    inner class LocalBinder : Binder() {
        fun getService(): SleepTimerService = this@SleepTimerService
    }

    override fun onBind(intent: Intent): IBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        isServiceRunning = true

        when (intent?.action) {

            START -> {
                isTimerRunning = true

                val millis: Long = intent.getLongExtra(MILLIS, 0)

                log("Millis in service: $millis")

                timerFlow = TimerFlow(millis)

                serviceScope.launch {
                    timerFlow.elapseTime.collect { millis ->
                        updateNotificationText(millis.to24HourFormat())

                        if (millis == 0L) { // timer finishes
                            log("Sleeping the device...")
                            updateNotificationText("Sleeping Device")
                            sleepTheDevice()
                        }
                    }
                }

                serviceScope.launch {
                    timerFlow.isRunning.collect { isRunning ->
                        updateNotificationAction(isTimerRunning = isRunning)
                        isTimerRunning = isRunning
                    }
                }

                serviceScope.launch { timerFlow.start() }

                val notification = getInitialTimerNotification(millis.to24HourFormat())

                startForeground(NOTIFICATION_ID, notification)
            }

            PAUSE -> pause()
            RESUME -> resume()
            RESET -> destroyService()
        }

        return START_STICKY
    }


    private fun sleepTheDevice() = serviceScope.launch {
        deviceSleeper.sleepDevice
            .onCompletion { destroyService() }
            .flowOn(Dispatchers.IO)
            .collect { log("Sleeping...") }
    }

    fun destroyService() {
        stopSelf()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("SleepTimer onDestroy")
        isServiceRunning = false
        isTimerRunning = false
        timerFlow.reset()
        notificationManager.cancelAll()
        serviceScope.cancel()
    }

    private fun getInitialTimerNotification(millis: String): Notification {
        val session = MediaSessionCompat(this, "tag").sessionToken

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(session)

        val activityIntent = PendingIntent.getActivity(
            this,
            1,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return notificationBuilder.apply {
            setSmallIcon(R.drawable.alarm_icon)
            addAction(R.drawable.pause, "Pause", createBroadcastIntent(PAUSE))
            addAction(R.drawable.reset, "Reset", createBroadcastIntent(RESET))
            setSubText("Sound Options")
            setContentTitle("Sleep Lock")
            setContentText(millis)
            setStyle(mediaStyle)
            setOngoing(true)
            setContentIntent(activityIntent)
        }.build()
    }


    private fun updateNotificationText(newText: String) {
        notificationBuilder.setContentText(newText).build()
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    //Gotta create a new builder since you can't replace notification actions :/
    private fun updateNotificationAction(isTimerRunning: Boolean) {
        if (isTimerRunning) {
            notificationBuilder
                .clearActions()
                .addAction(R.drawable.pause, "Pause", createBroadcastIntent(PAUSE))
                .addAction(
                    R.drawable.reset,
                    "Reset",
                    createBroadcastIntent(WhiteNoiseService.RESET)
                )
        } else {
            notificationBuilder
                .clearActions()
                .addAction(R.drawable.play, "Resume", createBroadcastIntent(RESUME))
                .addAction(
                    R.drawable.reset,
                    "Reset",
                    createBroadcastIntent(WhiteNoiseService.RESET)
                )
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }


    private fun createBroadcastIntent(action: String): PendingIntent {
        val intent = Intent(this, SleepTimerServiceReceiver::class.java).apply {
            this.action = action
        }

        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun pause() {
        timerFlow.pause()
    }

    fun resume() {
        timerFlow.resume()
    }
}