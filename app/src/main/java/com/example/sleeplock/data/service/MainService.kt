package com.example.sleeplock.data.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.R
import com.example.sleeplock.data.features.Timer
import com.example.sleeplock.data.features.WhiteNoise
import com.example.sleeplock.data.receiver.NotificationBroadcastReceiver
import com.example.sleeplock.ui.MainActivity
import com.example.sleeplock.ui.isAppInForeground
import com.example.sleeplock.utils.*
import io.reactivex.rxkotlin.subscribeBy

class MainService : Service() {

    private val binder = LocalBinder()

    private var isTimerAndSoundCreated = false
    private val isMainServiceRunning = MutableLiveData<Boolean>(false)

    private lateinit var timer: Timer
    private lateinit var whiteNoise: WhiteNoise

    private var playOrPause = 0

    private lateinit var contentIntent: PendingIntent
    private lateinit var pendingPlayIntent: PendingIntent
    private lateinit var pendingPauseIntent: PendingIntent
    private lateinit var pendingResetIntent: PendingIntent

    private val currentTime = MutableLiveData<Long>()
    private val timerStarted = MutableLiveData<Boolean>()
    private val timerPaused = MutableLiveData<Boolean>()
    private val timerCompleted = MutableLiveData<Boolean>()
    private val isBound = MutableLiveData<Boolean>()


    override fun onCreate() {
        super.onCreate()
        isMainServiceRunning.postValue(true)

        contentIntent = createActivityPendingIntent()
        pendingPlayIntent = createBroadcastPendingIntent(ACTION_PLAY)
        pendingPauseIntent = createBroadcastPendingIntent(ACTION_PAUSE)
        pendingResetIntent = createBroadcastPendingIntent(ACTION_RESET)
    }

    override fun onBind(intent: Intent?): IBinder? {
        isBound.value = true
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound.value = false // will remove the mediator Live Data's sources in the Repository
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        isBound.value = true
    }

    override fun onDestroy() {
        super.onDestroy()
        isBound.value = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //We ensure'd that no null values are sent to the service, but if they are than something went horribly wrong and it's better to crash the app
        val millis: Long = intent?.extras?.getLong(MILLIS)!!
        val index: Int = intent.extras?.getInt(INDEX)!!

        // Prevents duplication on sound & timer
        if (!isTimerAndSoundCreated) {
            createAndObserveTimer(millis)
            createSound(index)
            isTimerAndSoundCreated = true
        }

        when (intent.action) {
            ACTION_PLAY -> {
                startSoundAndTimer()
                playOrPause = 1
            }
            ACTION_PAUSE -> {
                pauseSoundAndTimer()
                playOrPause = 0
            }
            ACTION_RESET -> {
                timer.reset() // will trigger reset all
                return START_NOT_STICKY
            }
        }

        val time: String? = currentTime.value?.formatTime()

        startForeground(NOTIFICATION_ID, getMyNotification(time ?: "00:00", playOrPause))
        return START_STICKY
    }

    // Updates the notification content description with the text provided
    private fun updateNotification(text: String, playOrPause: Int) {
        val notification = getMyNotification(text, playOrPause)

        NotificationManagerCompat.from(this).apply {

            notify(NOTIFICATION_ID, notification)
        }
    }

    private fun getMyNotification(newText: String, playOrPause: Int): Notification {

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.music)
            .addAction(R.drawable.play, "Start", pendingPlayIntent)
            .addAction(R.drawable.pause, "Pause", pendingPauseIntent)
            .addAction(R.drawable.reset, "Reset", pendingResetIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(playOrPause, 2)  // index of the actions
            )
            .setSubText("Sound Options")
            .setContentTitle("Sleep Lock")
            .setContentText(newText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.BLACK)
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createSound(index: Int) {
        whiteNoise = WhiteNoise(MediaPlayer(), this, index)
    }

    private fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)

        timer.currentTime.subscribeBy(
            onNext = { timeInMilli ->
                currentTime.postValue(timeInMilli)
                updateNotification(timeInMilli.formatTime(), playOrPause)
            },
            onComplete = {

                //todo thought we deleted coroutine dependencies
//                GlobalScope.launch(Dispatchers.Main) { showFinishedToast(this@MainService, true) }

                currentTime.postValue(0)

                whiteNoise.reset()
                timerCompleted.postValue(true)

                resetAll()
            },
            onError = { Log.d("zwi", "Error observing the timer: $it") }
        )
    }

    fun startSoundAndTimer() {
        whiteNoise.start()
        timer.start()
        timerStarted.postValue(true)
    }

    fun pauseSoundAndTimer() {
        whiteNoise.pause()
        timer.pause()
        timerPaused.postValue(true)
    }

    fun resetSoundAndTimer() = timer.reset() // sound is reset on timer complete

    private fun resetAll() {
        resetBooleans()
        if (!isAppInForeground) terminateAll() // will terminate app if it's in the background
        stopSelf()
        stopForeground(true)
    }

    // Only exposes immutable properties
    fun getCurrentTime(): LiveData<Long> = currentTime

    fun getTimerStarted(): LiveData<Boolean> = timerStarted

    fun getTimerPaused(): LiveData<Boolean> = timerPaused

    fun getTimerCompleted(): LiveData<Boolean> = timerCompleted

    fun getIsBound(): LiveData<Boolean> = isBound

    fun getIsServiceRunning(): LiveData<Boolean> = isMainServiceRunning

    private fun createActivityPendingIntent(): PendingIntent =
        Intent(this, MainActivity::class.java).let { activityIntent ->
            PendingIntent.getActivity(
                this, 0, activityIntent, 0
            )
        }

    private fun createBroadcastPendingIntent(action: String): PendingIntent {
        val broadCastIntent = Intent(this, NotificationBroadcastReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(this, 0, broadCastIntent, 0)
    }

    private fun terminateAll() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun resetBooleans() {
        isTimerAndSoundCreated = false
        isMainServiceRunning.postValue(false)
    }

    inner class LocalBinder : Binder() {
        fun getService(): MainService = this@MainService
    }
}