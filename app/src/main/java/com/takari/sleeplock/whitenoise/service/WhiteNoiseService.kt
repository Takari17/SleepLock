package com.takari.sleeplock.whitenoise.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.app.NotificationCompat.MediaStyle
import coil.executeBlocking
import coil.imageLoader
import coil.request.ImageRequest
import com.takari.sleeplock.MainActivity
import com.takari.sleeplock.R
import com.takari.sleeplock.di.App
import com.takari.sleeplock.shared.TimerFlow
import com.takari.sleeplock.shared.log
import com.takari.sleeplock.shared.parcelable
import com.takari.sleeplock.shared.to24HourFormat
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


private var isServiceRunning = false
private var isTimerRunning = false



class WhiteNoiseService : Service() {

    companion object IntentActions {
        const val INIT_AND_START = "start"
        const val PAUSE = "pause"
        const val RESUME = "resume"
        const val RESET = "reset"
        const val MILLIS = "elapse time"
        const val WHITE_NOISE = "white noise"
        const val NOTIFICATION_ID = 46294

        fun isRunning() = isServiceRunning
        fun timerIsRunning() = isTimerRunning
    }


    lateinit var timerFlow: TimerFlow
    private lateinit var mediaPlayer: MediaPlayer
    private val notificationBuilder by lazy { NotificationCompat.Builder(this, App.CHANNEL_ID) }
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    lateinit var whiteNoise: WhiteNoise


    override fun onBind(intent: Intent): IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): WhiteNoiseService = this@WhiteNoiseService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        isServiceRunning = true

        log("Service intent action: ${intent.action}")

        when (intent.action) {

            INIT_AND_START -> {
                isTimerRunning = true

                whiteNoise = intent.parcelable(WHITE_NOISE)!!

                val millis: Long = intent.getLongExtra(MILLIS, 0)

                log("WhiteNoise: $whiteNoise, Milliseconds: $millis")

                mediaPlayer = MediaPlayer.create(this, whiteNoise.sound()).apply {
                    start()
                    isLooping = true
                }

                timerFlow = TimerFlow(millis)

                serviceScope.launch { observeTimerFlow(timerFlow) }

                serviceScope.launch { timerFlow.start() }

                val request = ImageRequest.Builder(this)
                    .data(whiteNoise.image())
                    .target { drawable -> drawable.toBitmap() }
                    .build()

                val bitmap = imageLoader.executeBlocking(request).drawable!!.toBitmap()

                val notification = getAndBuildTimerNotification(millis.to24HourFormat(), bitmap)

                startForeground(NOTIFICATION_ID, notification)
            }

            PAUSE -> pause()

            RESUME -> resume()

            RESET -> destroyService()
        }

        return START_STICKY
    }

    fun destroyService() {
        stopSelf()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerFlow.reset()
        mediaPlayer.reset()
        mediaPlayer.release() //must be released to avoid memory leaks
        isServiceRunning = false
        isTimerRunning = false
        notificationManager.cancelAll()
        serviceScope.cancel()
    }

    private fun getAndBuildTimerNotification(currentTime: String, bitMap: Bitmap): Notification {
        val activityIntent = PendingIntent.getActivity(
            this, 1, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )

        val session = MediaSessionCompat(this, "tag").sessionToken
        val mediaStyle = MediaStyle().setMediaSession(session)

        return notificationBuilder.apply {
            setSmallIcon(R.drawable.alarm_icon)
            addAction(R.drawable.pause, "Pause", createBroadcastIntent(PAUSE))
            addAction(R.drawable.reset, "Reset", createBroadcastIntent(RESET))
            setLargeIcon(bitMap)
            setStyle(mediaStyle)
            setSubText("Sound Options")
            setContentTitle("SleepLock")
            setContentText(currentTime)
            setContentIntent(activityIntent)
        }.build()
    }

    private fun updateNotificationText(newText: String) {
        notificationBuilder.setContentText(newText).setSmallIcon(R.drawable.alarm_icon).build()

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun updateNotificationAction(isTimerRunning: Boolean) {
        if (isTimerRunning) {
            notificationBuilder.clearActions()
                .addAction(R.drawable.pause, "Pause", createBroadcastIntent(PAUSE))
                .addAction(R.drawable.reset, "Reset", createBroadcastIntent(RESET))
        } else {
            notificationBuilder.clearActions()
                .addAction(R.drawable.play, "Resume", createBroadcastIntent(RESUME))
                .addAction(R.drawable.reset, "Reset", createBroadcastIntent(RESET))
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createBroadcastIntent(action: String): PendingIntent {
        val intent = Intent(this, WhiteNoiseServiceReceiver::class.java).apply {
            this.action = action
        }

        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private suspend fun observeTimerFlow(timerFlow: TimerFlow): Nothing =
        timerFlow.get.collect { timerState ->
                updateNotificationText(timerState.elapseTime.to24HourFormat())
                updateNotificationAction(isTimerRunning = timerState.isTimerRunning)

                isTimerRunning = timerState.isTimerRunning

                if (timerState.elapseTime == 0L) destroyService()
            }


    fun pause() {
        timerFlow.pause()
        mediaPlayer.pause()
    }

    fun resume() {
        timerFlow.resume()
        mediaPlayer.start()
    }
}
