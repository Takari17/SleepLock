package com.example.sleeplock.model.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.R
import com.example.sleeplock.feature.Timer
import com.example.sleeplock.ui.MainActivity
import com.example.sleeplock.utils.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var isServiceRunning = false
val serviceTime = MutableLiveData<Long>()


var foregroundTimerRunning = false
var foregroundTimerPaused = false

var serviceTimerPaused = false

class MyService : Service() {

    private var isTimerCreated = false
    private lateinit var timer: Timer

    private var currentTimeMillis: Long = 0
    private var playOrPause = 0


    private lateinit var contentIntent: PendingIntent
    private lateinit var pendingPlayIntent: PendingIntent
    private lateinit var pendingPauseIntent: PendingIntent
    private lateinit var pendingResetIntent: PendingIntent

    private val NOTIFICATION_ID = 1001

    private var isServiceForceStopped = false


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        isServiceRunning = true

        val millis: Long? = intent?.extras?.getLong(CURRENT_TIME)

        if (!isTimerCreated) createAndObserveTimer(millis ?: 0)

        when (intent?.action) {
            ACTION_PLAY -> {
                startSoundAndTimer()
                playOrPause = 1
            }

            ACTION_PAUSE -> {
                pauseSoundAndTimer()
                playOrPause = 0
            }

            ACTION_RESET -> { // called from within service
                timer.resetTimer() // will trigger reset all
                return START_NOT_STICKY
            }

            ACTION_FORCE_STOP -> { // called from external source
                forceStop()
            }
        }

        val activityIntent = Intent(this, MainActivity::class.java)
        contentIntent = PendingIntent.getActivity(
            this,
            0, activityIntent, 0
        )

        val playIntent = getBroadcastReceiverIntent()
        playIntent.action = ACTION_PLAY
        pendingPlayIntent = createPendingIntent(playIntent)

        val pauseIntent = getBroadcastReceiverIntent()
        pauseIntent.action = ACTION_PAUSE
        pendingPauseIntent = createPendingIntent(pauseIntent)

        val resetIntent = getBroadcastReceiverIntent()
        resetIntent.action = ACTION_RESET
        pendingResetIntent = createPendingIntent(resetIntent)


        startForeground(NOTIFICATION_ID, getMyNotification(currentTimeMillis.formatTime(), playOrPause))
        return START_NOT_STICKY
    }

    // Updates the notification content description with the text provided
    private fun updateNotification(text: String, playOrPause: Int) {
        val notification = getMyNotification(text, playOrPause)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getMyNotification(text: String, playOrPause: Int): Notification {

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
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.BLACK)
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .build()
    }


    private fun startSoundAndTimer() {
        //        sound.startMediaPlayer();
        timer.startTimer()
        serviceTimerPaused = false
    }


    private fun pauseSoundAndTimer() {
        //        sound.pauseMediaPlayer();
        timer.pauseTimer()
        serviceTimerPaused = true
    }

    private fun resetAll() {
        timer.resetTimer()
        resetBooleans()
        stopSelf()
        stopForeground(true)
    }

    private fun forceStop() {
        isServiceForceStopped = true
        timer.resetTimer()
        isTimerCreated = false
        isServiceRunning = false
        stopSelf()
        stopForeground(true)
    }

    private fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)
        isTimerCreated = true

        timer.currentTime.subscribeBy(
            onNext = { timeInMilli ->
                currentTimeMillis = timeInMilli
                serviceTime.postValue(timeInMilli)
                updateNotification(timeInMilli.formatTime(), playOrPause)
            },
            onComplete = {
                if (!isServiceForceStopped) {
                    GlobalScope.launch(Dispatchers.Main) { showFinishedToast(this@MyService, true) }
                    killAppProcess()
                }
                resetAll()
            }
        )
    }

    private fun createPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(this, 0, intent, 0)
    }

    private fun getBroadcastReceiverIntent(): Intent {
        return Intent(this, NotificationBroadcastReceiver::class.java)
    }

    private fun killAppProcess() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun resetBooleans(){
        isTimerCreated = false
        isServiceRunning = false
        serviceTimerPaused = false
    }
}
