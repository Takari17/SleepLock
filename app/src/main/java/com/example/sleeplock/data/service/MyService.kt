package com.example.sleeplock.data.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.R
import com.example.sleeplock.data.features.SoundPlayer
import com.example.sleeplock.data.features.Timer
import com.example.sleeplock.data.receiver.NotificationBroadcastReceiver
import com.example.sleeplock.ui.MainActivity
import com.example.sleeplock.ui.isAppInForeground
import com.example.sleeplock.utils.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var isServiceRunning = false

class MyService : Service() {

    private val binder = LocalBinder()

    private var isTimerCreated = false
    private var isSoundCreated = false

    private lateinit var timer: Timer
    private lateinit var soundPlayer: SoundPlayer

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

    // Only exposes immutable properties
    fun getCurrentTime(): LiveData<Long> = currentTime
    fun getTimerStarted(): LiveData<Boolean> = timerStarted
    fun getTimerPaused(): LiveData<Boolean> = timerPaused
    fun getTimerCompleted(): LiveData<Boolean> = timerCompleted
    fun getIsBound(): LiveData<Boolean> = isBound


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

        isServiceRunning = true

        val millis: Long? = intent?.extras?.getLong(MILLIS)
        val index: Int? = intent?.extras?.getInt(INDEX)

        // Prevents duplication
        if (!isTimerCreated) createAndObserveTimer(millis ?: 0)
        if (!isSoundCreated && index != null) createSound(index)

        when (intent?.action) {
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

        val activityIntent = Intent(this, MainActivity::class.java)
        contentIntent = PendingIntent.getActivity(
            this,
            0, activityIntent, 0
        )

        //todo why are we doing this in this method? Do it on init since we only seen to set its values ONCE

        val playIntent = getBroadcastReceiverIntent().apply {
            action = ACTION_PLAY
            pendingPlayIntent = createPendingIntent(this)
        }


        val pauseIntent = getBroadcastReceiverIntent()
        pauseIntent.action = ACTION_PAUSE
        pendingPauseIntent = createPendingIntent(pauseIntent)

        val resetIntent = getBroadcastReceiverIntent()
        resetIntent.action = ACTION_RESET
        pendingResetIntent = createPendingIntent(resetIntent)

        val time = currentTime.value?.formatTime()

        startForeground(NOTIFICATION_ID, getMyNotification(time?: "00:00", playOrPause))
        return START_NOT_STICKY
    }

    private fun updateNotification(text: String, playOrPause: Int) {
        // Updates the notification content description with the text provided
        val notification = getMyNotification(text, playOrPause)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
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
        soundPlayer = SoundPlayer(this, index)
        isSoundCreated = true
    }

    private fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)
        isTimerCreated = true

        timer.currentTime.subscribeBy(
            onNext = { timeInMilli ->
                currentTime.postValue(timeInMilli)
                updateNotification(timeInMilli.formatTime(), playOrPause)
            },
            onComplete = {

                GlobalScope.launch(Dispatchers.Main) { showFinishedToast(this@MyService, true) }

                currentTime.postValue(0)

                soundPlayer.reset()
                timerCompleted.postValue(true)

                resetAll()
            },
            onError = { Log.d ("zwi", "Error observing the timer: $it")}
        )
    }

    fun startSoundAndTimer() {
        soundPlayer.start()
        timer.start()
        timerStarted.postValue(true)
    }

    fun pauseSoundAndTimer() {
        soundPlayer.pause()
        timer.pause()
        timerPaused.postValue(true)
    }

    fun resetTimer() = timer.reset() // sound is reset on timer complete

    private fun resetAll() {
        resetBooleans()
        if (!isAppInForeground) terminateAll() // will terminate app if it's in the background
        stopSelf()
        stopForeground(true)
    }

    private fun createPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(this, 0, intent, 0)
    }

    private fun getBroadcastReceiverIntent(): Intent {
        return Intent(this, NotificationBroadcastReceiver::class.java)
    }

    private fun terminateAll() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun resetBooleans() {
        isTimerCreated = false
        isServiceRunning = false
    }

    inner class LocalBinder : Binder() {
        fun getService(): MyService = this@MyService
    }
}