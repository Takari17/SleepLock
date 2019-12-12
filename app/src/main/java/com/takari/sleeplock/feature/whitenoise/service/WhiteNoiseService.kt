package com.takari.sleeplock.feature.whitenoise.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.takari.sleeplock.App.Companion.applicationComponent
import com.takari.sleeplock.feature.common.*
import com.takari.sleeplock.feature.isAppInBackground
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import io.reactivex.rxkotlin.subscribeBy


class WhiteNoiseService : Service() {

    private lateinit var timer: Timer
    private lateinit var whiteNoisePlayer: MediaPlayer
    private lateinit var notification: MediaPlayerNotification
    private lateinit var resumeOrPause: MediaPlayerNotification.ResumeOrPause
    private val repository = applicationComponent.whiteNoiseRepository

    //used for keeping the notification's time in sync with the timer's when paused.
    private var recentTime: Long = 0


    override fun onCreate() {
        super.onCreate()
        running = true

        notification = MediaPlayerNotification(
            1011, this,
            createBroadcastPendingIntent(ServiceControls.Resume.name),
            createBroadcastPendingIntent(ServiceControls.Pause.name),
            createBroadcastPendingIntent(ServiceControls.Reset.name)
        )
    }

    override fun onBind(intent: Intent): IBinder? {
        return LocalBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent != null) {

            when (intent.action) {

                ServiceControls.Start.name -> {

                    val millis: Long = intent.getLongExtra(WHITE_NOISE_TIME, 0)
                    val whiteNoise: WhiteNoise = intent.getParcelableExtra(WHITE_NOISE)

                    whiteNoisePlayer = MediaPlayer.create(this, whiteNoise.rawFile())

                    timer = Timer(millis, { isRunning -> setIsRunning(isRunning) }, { hasStarted ->
                        setHasStarted(hasStarted)
                    })

                    timer.currentTime.subscribeBy(
                        onNext = { time ->
                            recentTime = time
                            repository.currentTime.accept(time)
                            notification.update(time.to24HourFormat())
                        },
                        onComplete = { reset() },
                        onError = { logD("error observing timer in WhiteNoiseService: $it") }
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
        }

        startForeground(
            notification.id,
            notification.notification(
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
        whiteNoisePlayer.start()
        whiteNoisePlayer.isLooping = true
    }

    fun pause() {
        timer.pause()
        whiteNoisePlayer.pause()
    }

    fun resume() {
        timer.resume()
        whiteNoisePlayer.start()
    }

    fun reset() {
        timer.reset()
        whiteNoisePlayer.reset()
        repository.reset()
        destroyService()
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

    private fun destroyService() {
        stopSelf()
        stopForeground(true)
        if (isAppInBackground) terminateAll()
    }

    private fun terminateAll() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun createBroadcastPendingIntent(action: String): PendingIntent =
        PendingIntent.getBroadcast(
            this,
            0,
            WhiteNoiseServiceReceiver.createIntent(
                this,
                action
            ),
            0
        )


    inner class LocalBinder : Binder() {
        fun getService(): WhiteNoiseService = this@WhiteNoiseService
    }

    companion object {
        const val WHITE_NOISE_TIME = "timer id"
        const val WHITE_NOISE = "white noise id"
        fun isRunning() = running
    }
}

private var running = false