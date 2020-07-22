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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media.app.NotificationCompat.MediaStyle
import com.bumptech.glide.Glide
import com.takari.sleeplock.App
import com.takari.sleeplock.R
import com.takari.sleeplock.main.MainActivity
import com.takari.sleeplock.shared.TimerFlow
import com.takari.sleeplock.shared.to24HourFormat
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion


class WhiteNoiseService : Service() {

    companion object {
        const val INIT_AND_START = "start"
        const val PAUSE = "pause"
        const val RESUME = "resume"
        const val RESET = "reset"
        const val MILLIS = "timerFlow id"
        const val WHITE_NOISE = "white noise id"

        /**Immutable so it's pretty reliable to use.*/
        fun isRunning() = isServiceRunning
    }

    private lateinit var timerFlow: TimerFlow
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var whiteNoise: WhiteNoise

    private val mediaStyle: MediaStyle by lazy {
        val session = MediaSessionCompat(this, "tag").sessionToken
        MediaStyle().setMediaSession(session)
    }

    private lateinit var notificationJob: Job

    /*
    Using the same builder when updating a notification makes the whole operation less costly to the
    UI thread since it doesn't need to create a new builder each time.
     */
    private val notificationBuilder by lazy { NotificationCompat.Builder(this, App.CHANNEL_ID) }
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val id = 46294

    // the view observes these when it binds
    private val _elapseTime: MutableLiveData<Long> = MutableLiveData(0L)
    val elapseTime: LiveData<Long> = _elapseTime

    private val _isTimerRunning: MutableLiveData<Boolean> = MutableLiveData(true)
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    var onServiceDestroyed: (Unit) -> Unit = {}


    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
    }

    override fun onBind(intent: Intent): IBinder? = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): WhiteNoiseService = this@WhiteNoiseService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {

            INIT_AND_START -> {

                whiteNoise = intent.getParcelableExtra(WHITE_NOISE)
                val millis: Long = intent.getLongExtra(MILLIS, 0)

                mediaPlayer = MediaPlayer.create(this, whiteNoise.sound()).apply {
                    start()
                    isLooping = true
                }

                timerFlow = TimerFlow(millis) { isTimerRunning ->
                    serviceScope.launch(Dispatchers.Main) {
                        updateNotificationAction(isTimerRunning)
                        _isTimerRunning.value = isTimerRunning

                    }
                }

                serviceScope.launch { observeTimerFlow(timerFlow) }

                notificationJob = serviceScope.launch {

                    val bitMap: Bitmap = Glide.with(this@WhiteNoiseService)
                        .asBitmap()
                        .load(whiteNoise.image())
                        .submit()
                        .get()

                    withContext(Dispatchers.Main) {
                        val notification = getTimerNotification(0L.to24HourFormat(), bitMap)
                        startForeground(id, notification)
                    }
                }
            }
            PAUSE -> pause()

            RESUME -> resume()

            RESET -> destroyService()
        }

        return START_STICKY
    }

    fun destroyService() {
        stopSelf()
        stopForeground(true)
    }

    //can only be called once
    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        onServiceDestroyed(Unit)
        _elapseTime.value = 0
        timerFlow.reset()
        mediaPlayer.reset()
        mediaPlayer.release() //must be released to avoid memory leaks
        notificationManager.cancelAll()
        serviceScope.cancel()
    }

    private fun getTimerNotification(currentTime: String, bitMap: Bitmap): Notification {
        mediaStyle.showPauseAndResetActions()

        return notificationBuilder.apply {
            setSmallIcon(R.drawable.alarm_icon)
            //will always be "pause" initially
            addAction(R.drawable.pause, "Pause", createBroadcastIntent(PAUSE))
            addAction(R.drawable.play, "Resume", createBroadcastIntent(RESUME))
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
        notificationBuilder.setContentText(newText).build()

        notificationManager.notify(id, notificationBuilder.build())
    }

    private fun updateNotificationAction(timerRunning: Boolean) {

        if (timerRunning) mediaStyle.showPauseAndResetActions()
        else mediaStyle.showPlayAndResetActions()

        notificationBuilder.setStyle(mediaStyle)

        notificationManager.notify(id, notificationBuilder.build())
    }

    //Makes these operation much more explicit and concrete
    private fun MediaStyle.showPauseAndResetActions() {
        this.setShowActionsInCompactView(0, 2)
    }

    private fun MediaStyle.showPlayAndResetActions() {
        this.setShowActionsInCompactView(1, 2)
    }

    private val activityIntent by lazy {
        val intent = Intent(this, MainActivity::class.java)
        PendingIntent.getActivity(this, 1, intent, 0)
    }

    private fun createBroadcastIntent(action: String): PendingIntent {
        val intent = Intent(this, WhiteNoiseServiceReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(this, 0, intent, 0)
    }

    private suspend fun observeTimerFlow(timerFlow: TimerFlow) = timerFlow.get
        .onCompletion { destroyService() }
        .collect { millis ->
            withContext(Dispatchers.Main) {
                _elapseTime.value = millis

                if (notificationJob.isCompleted) updateNotificationText(millis.to24HourFormat())
            }
        }

    fun getWhiteNoise(): WhiteNoise? = whiteNoise

    fun pause() {
        timerFlow.pause()
        mediaPlayer.pause()
    }

    fun resume() {
        timerFlow.resume()
        mediaPlayer.start()
    }
}

private var isServiceRunning = false