package com.takari.sleeplock.feature.sleeptimer.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.takari.sleeplock.App
import com.takari.sleeplock.App.Companion.applicationComponent
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.MainActivity
import com.takari.sleeplock.feature.common.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit


class SleepTimerService : Service() {

    private lateinit var timer: Timer
    private lateinit var notificationAction: NotificationAction
    private val repository = applicationComponent.sleepTimerRepository
    private val volumeManager = applicationComponent.volumeManager
    private val screenManager = applicationComponent.screenManager
    private val compositeDisposable = CompositeDisposable()
    private var notificationId = 3356
    private var recentTime: Long = 0


    override fun onCreate() {
        super.onCreate()
        running = true
    }

    override fun onBind(intent: Intent): IBinder? = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {

            ServiceControls.Start.name -> {

                val millis: Long = intent.getLongExtra(SLEEP_TIMER_TIME, 0)

                timer = Timer(millis, { isRunning -> setIsRunning(isRunning) }, { hasStarted ->
                    setHasStarted(hasStarted)
                })

                compositeDisposable += timer.currentTime
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onNext = { time ->
                            recentTime = time
                            repository.currentTime.accept(time)

                            //updates notification
                            NotificationManagerCompat.from(this)
                                .notify(
                                    notificationId,
                                    notification(notificationAction, time.to24HourFormat())
                                )
                        },
                        onComplete = { reset() },
                        onError = { logD("error observing currentTime in SleepTimerService: $it") }

                    )

                notificationAction = NotificationAction.Pause
                start()
            }

            ServiceControls.Pause.name -> {
                notificationAction = NotificationAction.Resume
                pause()
            }

            ServiceControls.Resume.name -> {
                notificationAction = NotificationAction.Pause
                resume()
            }

            ServiceControls.Reset.name -> {
                reset()
                return START_STICKY
            }
        }

        startForeground(
            notificationId,
            notification(notificationAction, recentTime.to24HourFormat())
        )
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false
        compositeDisposable.clear()
    }


    private fun notification(action: NotificationAction, currentTime: String): Notification =
        NotificationCompat.Builder(this, App.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.alarm_icon)
            if (action == NotificationAction.Resume)
                addAction(R.drawable.play, "Pause", broadcastIntent(ServiceControls.Resume.name))
            else
                addAction(R.drawable.pause, "Resume", broadcastIntent(ServiceControls.Pause.name))
            addAction(R.drawable.reset, "Reset", broadcastIntent(ServiceControls.Reset.name))
            setStyle(MediaStyle().setShowActionsInCompactView(0, 1))
            setSubText("Sound Options")
            setContentTitle("SleepLock")
            setContentText(currentTime)
            setContentIntent(mainActivityIntent())
        }.build()


    private fun mainActivityIntent() = PendingIntent.getActivity(
        this,
        1,
        MainActivity.createIntent(this),
        0
    )

    fun start() {
        timer.start()
    }

    fun pause() {
        timer.pause()
    }

    fun resume() {
        timer.resume()
    }

    fun reset() {
        timer.reset()
        repository.completed.accept(Unit)
        destroyService()

        //sleeps the device
        Observable.interval(2, TimeUnit.SECONDS)
            .take(15)
            .subscribeBy(
                onNext = { volumeManager.lowerVolume() },
                onComplete = { screenManager.turnOffScreen() },
                onError = { logD("Error trying to sleep the device: $it") }
            )
    }


    private fun destroyService() {
        stopSelf()
        stopForeground(true)
    }

    private fun setIsRunning(isRunning: Boolean) {
        if (isRunning)
            repository.timerAction.accept(Timer.Action.Pause)
        else
            repository.timerAction.accept(Timer.Action.Resume)

        repository.isTimerRunning = isRunning
    }

    private fun setHasStarted(hasStarted: Boolean) {
        repository.hasTimerStarted = hasStarted
    }

    private fun broadcastIntent(action: String): PendingIntent =
        PendingIntent.getBroadcast(
            this, 0, SleepTimerServiceReceiver.createIntent(
                this,
                action
            ),
            0
        )

    inner class LocalBinder : Binder() {
        fun getService(): SleepTimerService = this@SleepTimerService
    }

    companion object {
        const val SLEEP_TIMER_TIME = "sleep timer timey tuper timor time"
        fun isRunning() = running
    }
}

private var running = false