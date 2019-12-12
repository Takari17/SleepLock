package com.takari.sleeplock.feature.sleeptimer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.takari.sleeplock.App.Companion.applicationComponent
import com.takari.sleeplock.feature.common.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class SleepTimerService : Service() {

    private lateinit var timer: Timer
    private lateinit var mediaPlayerNotif: MediaPlayerNotification
    private lateinit var resumeOrPause: MediaPlayerNotification.ResumeOrPause
    private val repository = applicationComponent.sleepTimerRepository
    private val volumeManager = applicationComponent.volumeManager
    private val screenManager = applicationComponent.screenManager

    //used for keeping the notification's time in sync with the timer's when paused.
    private var recentTime: Long = 0


    override fun onCreate() {
        super.onCreate()
        running = true
        mediaPlayerNotif = MediaPlayerNotification(
            id = 2348,
            context = this,
            resumeIntent = createBroadcastPendingIntent(ServiceControls.Resume.name),
            pauseIntent = createBroadcastPendingIntent(ServiceControls.Pause.name),
            resetIntent = createBroadcastPendingIntent(ServiceControls.Reset.name)
        )
    }

    override fun onBind(intent: Intent): IBinder? {
        return LocalBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {

            ServiceControls.Start.name -> {

                val millis: Long = intent.getLongExtra(SLEEP_TIMER_TIME, 0)

                timer = Timer(millis, { isRunning -> setIsRunning(isRunning) }, { hasStarted ->
                    setHasStarted(hasStarted)
                })

                timer.currentTime.subscribeBy(
                        onNext = { time ->
                            recentTime = time
                            repository.currentTime.accept(time)
                            mediaPlayerNotif.update(time.to24HourFormat())
                        },
                        onComplete = { reset() },
                        onError = { logD("error observing currentTime in SleepTimerService: $it") }

                    )

                resumeOrPause = MediaPlayerNotification.ResumeOrPause.Pause
                start()
            }

            ServiceControls.Pause.name -> {
                resumeOrPause = MediaPlayerNotification.ResumeOrPause.Pause
                pause()
            }

            ServiceControls.Resume.name -> {
                resumeOrPause = MediaPlayerNotification.ResumeOrPause.Resume
                resume()
            }

            ServiceControls.Reset.name -> {
                reset()
                return START_STICKY
            }
        }

        startForeground(
            mediaPlayerNotif.id,
            mediaPlayerNotif.notification(
                recentTime.to24HourFormat(),
                resumeOrPause
            )
        )
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false
    }

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
        sleepDevice()
        repository.completed.accept(Unit)
        destroyService()
    }

    private fun sleepDevice() {
        Observable.interval(1, TimeUnit.SECONDS)
            .take(15)
            .subscribeBy(
                onNext = { volumeManager.lowerVolume() },
                onComplete = { screenManager.turnOffScreen() },
                onError = { logD("Error in SleepDevice: $it") }
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

    private fun createBroadcastPendingIntent(action: String): PendingIntent =
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