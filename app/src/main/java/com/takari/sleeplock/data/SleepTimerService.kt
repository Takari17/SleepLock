package com.takari.sleeplock.data

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jakewharton.rxrelay2.BehaviorRelay
import com.takari.sleeplock.App.Companion.CHANNEL_ID
import com.takari.sleeplock.R
import com.takari.sleeplock.data.whitenoise.WhiteNoisePlayer
import com.takari.sleeplock.data.timer.TimerBroadcastReceiver
import com.takari.sleeplock.data.timer.Timer
import com.takari.sleeplock.ui.MainActivity
import com.takari.sleeplock.ui.isAppInBackground
import com.takari.sleeplock.utils.formatTime
import com.takari.sleeplock.utils.getResourceString
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 *  Creates and controls a timer and white noise instance. Allows the timer and white noise to
 *  continue running even if the app is closed. Bind to this service to control the instances
 *  and or observe the timer's callbacks.
 */
class SleepTimerService : Service() {

    companion object{
        const val WHITE_NOISE = "sound"
        const val TIME = "time"
    }

    private lateinit var timer: Timer
    private lateinit var whiteNoisePlayer: WhiteNoisePlayer

    // Used for alternating between the play and pause notification actions. The number represents the index of our action list we want to set.
    private var notificationAction = NotificationAction.Play.index

    private val currentTime = BehaviorRelay.create<Long>()

    private val isTimerRunning = BehaviorRelay.create<Boolean>()

    private val hasTimerStarted = BehaviorRelay.create<Boolean>()

    private val timerCompleted = BehaviorRelay.create<Unit>()

    private val notificationID = 1001


    override fun onBind(intent: Intent): IBinder? = LocalBinder()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {

            IntentAction.START.name -> {
                //I ensure'd that no null values are sent to the service by this time.
                val chosenTime: Long = intent.extras?.getLong(TIME)!!
                val whiteNoise: Int = intent.extras?.getInt(WHITE_NOISE)!!

                createAndObserveTimer(chosenTime)
                createWhiteNoisePlayer(whiteNoise)

                startSoundAndTimer()

                notificationAction - NotificationAction.Pause.index
            }
            //These intent actions are the for notification buttons onClick functionality.
            IntentAction.PAUSE.name -> {
                pauseSoundAndTimer()

                notificationAction = NotificationAction.Play.index
            }
            IntentAction.RESUME.name -> {
                resumeSoundAndTimer()

                notificationAction = NotificationAction.Pause.index
            }
            IntentAction.RESET.name -> {
                resetSoundAndTimer() // will trigger reset all

                return START_NOT_STICKY
            }
        }

        val timeString: String? = currentTime.value?.formatTime()

        startForeground(
            notificationID,
            getForegroundNotification(timeString ?: "00:00", notificationAction)
        )
        return START_STICKY
    }


    // Updates the notification text and action
    private fun updateNotification(text: String, playOrPause: Int) =
        NotificationManagerCompat.from(this).apply {

            notify(notificationID, getForegroundNotification(text, playOrPause))
        }


    private fun getForegroundNotification(newText: String, playOrPause: Int) =

        NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.alarm_icon)
            addAction(
                R.drawable.play,
                getResourceString(this@SleepTimerService, R.string.start),
                createBroadcastPendingIntent(IntentAction.RESUME.name)
            )
            addAction(
                R.drawable.pause,
                getResourceString(this@SleepTimerService, R.string.pause),
                createBroadcastPendingIntent(IntentAction.PAUSE.name)
            )
            addAction(
                R.drawable.reset,
                getResourceString(this@SleepTimerService, R.string.reset),
                createBroadcastPendingIntent(IntentAction.RESET.name)
            )
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(playOrPause, 2)  // index of the actions
            )
            setSubText(getResourceString(this@SleepTimerService, R.string.subText))
            setContentTitle(getResourceString(this@SleepTimerService, R.string.contentTitle))
            setContentText(newText)
            setContentIntent(createActivityPendingIntent())
        }.build()


    private fun createWhiteNoisePlayer(whiteNoise: Int) {
        whiteNoisePlayer = WhiteNoisePlayer(whiteNoise, this)
    }


    private fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)

        timer.countDownTimer
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { timeInMilli ->
                    currentTime.accept(timeInMilli)
                    updateNotification(timeInMilli.formatTime(), notificationAction)
                },
                onError = { Log.d("zwi", "Error observing countDownTimer in SleepTimerService: $it") }
            )

        timer.getCompleted()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    timerCompleted.accept(Unit)
                    resetAll()
                },
                onError = { Log.d("zwi", "Error observing completed in SleepTimerService: $it") }
            )

        timer.getHasStarted()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { hasStarted -> hasTimerStarted.accept(hasStarted) },
                onError = { Log.d("zwi", "Error observing hasStarted in SleepTimerService: $it") }
            )

        timer.getIsRunning()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { isRunning -> isTimerRunning.accept(isRunning) },
                onError = { Log.d("zwi", "Error observing isRunning in SleepTimerService: $it") }
            )
    }

    private fun startSoundAndTimer() {
        whiteNoisePlayer.start()
        timer.start()
    }

    fun pauseSoundAndTimer() {
        whiteNoisePlayer.pause()
        timer.pause()
    }

    fun resumeSoundAndTimer() {
        whiteNoisePlayer.start()
        timer.resume()
    }

    // White sound is reset on timer complete
    fun resetSoundAndTimer() = timer.reset()

    private fun resetAll() {
        currentTime.accept(0)
        whiteNoisePlayer.reset()
        stopSelf()
        stopForeground(true)
        if (isAppInBackground) terminateAll()
    }

    private fun createActivityPendingIntent(): PendingIntent =
        PendingIntent.getActivity(
            this,
            1,
            MainActivity.createIntent(this),
            0
        )

    private fun createBroadcastPendingIntent(action: String): PendingIntent =
        PendingIntent.getBroadcast(
            this,
            0,
            TimerBroadcastReceiver.createIntent(this, action),
            0
        )

    fun getCurrentTime(): Observable<Long> = currentTime

    fun getIsTimerRunning(): Observable<Boolean> = isTimerRunning

    fun getHasTimerStarted(): Observable<Boolean> = hasTimerStarted

    fun getTimerCompleted(): Observable<Unit> = timerCompleted


    private fun terminateAll() = android.os.Process.killProcess(android.os.Process.myPid())

    /**
     * Represents the intent actions the service will respond to.
     */
    enum class IntentAction {
        START, PAUSE, RESUME, RESET
    }

    /**
     * Represents the options for setting the notification action, the index represents the index of
     * our action we want to set from the action list.
     */
    enum class NotificationAction(val index: Int){
        Play(0), Pause(1)
    }
    inner class LocalBinder : Binder() {
        fun getService(): SleepTimerService = this@SleepTimerService
    }
}