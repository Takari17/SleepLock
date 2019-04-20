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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sleeplock.view.MainActivity
import com.example.sleeplock.R
import com.example.sleeplock.feature.Timer
import com.example.sleeplock.model.util.Constants
import com.example.sleeplock.model.util.Constants.*
import com.example.sleeplock.model.util.formatTime
import com.example.sleeplock.model.util.showFinishedToast
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var isServiceRunning = false

class CustomService : Service() {

    private val localBroadcastManager = LocalBroadcastManager.getInstance(this)

    private var isTimerCreated = false
    private lateinit var timer: Timer

    private var currentTimeMillis: Long = 0
    private var playOrPause = 0


    private lateinit var contentIntent: PendingIntent
    private lateinit var pendingPlayIntent: PendingIntent
    private lateinit var pendingPauseIntent: PendingIntent
    private lateinit var pendingResetIntent: PendingIntent

    private val NOTIFICATION_ID = 1001


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        isServiceRunning = true

        val millis: Long? = intent?.extras?.getLong(Constants.CURRENT_TIME.text)

        if (!isTimerCreated) createAndObserveTimer(millis ?: 0)

        when (intent?.action) {
            ACTION_PLAY.text -> {
                startSoundAndTimer()
                playOrPause = 1
            }

            ACTION_PAUSE.text -> {
                pauseSoundAndTimer()
                playOrPause = 0
            }

            ACTION_RESET.text -> {
                resetAll()
                return START_NOT_STICKY
            }
        }

        val activityIntent = Intent(this, MainActivity::class.java)
        contentIntent = createPendingIntent(activityIntent)

        val playIntent = getBroadcastReceiverIntent()
        playIntent.action = ACTION_PLAY.text
        pendingPlayIntent = createPendingIntent(playIntent)

        val pauseIntent = getBroadcastReceiverIntent()
        pauseIntent.action = ACTION_PAUSE.text
        pendingPauseIntent = createPendingIntent(pauseIntent)

        val resetIntent = getBroadcastReceiverIntent()
        resetIntent.action = ACTION_RESET.text
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

        return NotificationCompat.Builder(this, CHANNEL_ID.text)
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
    }


    private fun pauseSoundAndTimer() {
        //        sound.pauseMediaPlayer();
        timer.pauseTimer()
    }


    private fun resetAll() {
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
            onNext = {
                currentTimeMillis = it
                updateNotification(it.formatTime(), playOrPause)
            },
            onComplete = {
                resetAll()
                sendTimeBroadcast(currentTimeMillis)
                GlobalScope.launch(Dispatchers.Main) { showFinishedToast(this@CustomService) }
            }
        )


    }

    private fun sendTimeBroadcast(millis: Long) {
        // received by Fragment
        Intent().apply {
            action = MILLIS.text
            putExtra("millis", millis)
            localBroadcastManager.sendBroadcast(this)
        }
    }


    private fun createPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(this, 0, intent, 0)
    }

    private fun getBroadcastReceiverIntent(): Intent {
        return Intent(this, NotificationBroadcastReceiver::class.java)
    }
}
