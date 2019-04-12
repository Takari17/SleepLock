package com.example.sleeplock

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sleeplock.Constants.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var isServiceRunning = false

class CustomService : Service() {


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

        val action = intent?.action

        when (action) {
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
                return Service.START_NOT_STICKY
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
        return Service.START_NOT_STICKY
    }

    private fun updateNotification(text: String, playOrPause: Int) {
        // Updates the notification content description with the text provided
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
                    .setShowActionsInCompactView(playOrPause, 2)
            ) // index of the actions
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
                sendBroadcast(currentTimeMillis)
                GlobalScope.launch(Dispatchers.Main) { showFinishedToast(this@CustomService) }
            }
        )


    }

    private fun sendBroadcast(millis: Long) {
        val sendTimeToViewModel = Intent(MILLIS.text)
        sendTimeToViewModel.putExtra("millis", millis)
        sendBroadcast(sendTimeToViewModel)
    }


    private fun createPendingIntent(intent: Intent): PendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

    private fun getBroadcastReceiverIntent() = Intent(this, CustomBroadcastReceiver::class.java)


    /*
When an notification action is clicked, the broadcast receiver sets an action for the intent,
and invokes the startButton on command method in our service, which gets filtered in a when statement
 */

    class CustomBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val actionIntent = Intent(context, CustomService::class.java)

            when (action) {
                ACTION_PLAY.text -> {
                    actionIntent.action = action
                    context.startService(actionIntent)
                }

                ACTION_PAUSE.text -> {
                    actionIntent.action = action
                    context.startService(actionIntent)
                }

                ACTION_RESET.text -> {
                    actionIntent.action = action
                    context.startService(actionIntent)
                }
            }
        }


    }

}
