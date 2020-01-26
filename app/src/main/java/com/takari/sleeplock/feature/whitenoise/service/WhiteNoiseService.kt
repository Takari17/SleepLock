package com.takari.sleeplock.feature.whitenoise.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.takari.sleeplock.App
import com.takari.sleeplock.App.Companion.applicationComponent
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.MainActivity
import com.takari.sleeplock.feature.common.*
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy


class WhiteNoiseService : Service() {

    private lateinit var timer: Timer
    private lateinit var whiteNoise: WhiteNoise
    private lateinit var whiteNoisePlayer: MediaPlayer
    private lateinit var notificationAction: NotificationAction
    private var notificationId = 2468
    private val repository = applicationComponent.whiteNoiseRepository
    private val compositeDisposable = CompositeDisposable()
    private var recentTime: Long = 0


    override fun onCreate() {
        super.onCreate()
        running = true
    }

    override fun onBind(intent: Intent): IBinder? = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent != null) {

            when (intent.action) {

                ServiceControls.Start.name -> {

                    val millis: Long = intent.getLongExtra(WHITE_NOISE_TIME, 0)

                    whiteNoise = intent.getParcelableExtra(WHITE_NOISE)

                    whiteNoisePlayer = MediaPlayer.create(this, whiteNoise.rawFile())

                    timer = Timer(millis, { isRunning -> setIsRunning(isRunning) }, { hasStarted ->
                        setHasStarted(hasStarted)
                    })

                   compositeDisposable += timer.currentTime
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                        onNext = { time ->
                            recentTime = time
                            repository.currentTime.accept(time)

                            NotificationManagerCompat.from(this)
                                .notify(notificationId, notification(notificationAction, time.to24HourFormat()))
                        },
                        onComplete = { reset() },
                        onError = { logD("error observing timer in WhiteNoiseService: $it") }
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
        }

        //the id can stay static
        startForeground(notificationId, notification(notificationAction, recentTime.to24HourFormat()))
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
            setLargeIcon(BitmapFactory.decodeResource(resources, whiteNoise.image()))
            addAction(R.drawable.reset, "Reset", broadcastIntent(ServiceControls.Reset.name))
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1)
                    .setMediaSession(MediaSessionCompat(this@WhiteNoiseService, "tag").sessionToken)
            )
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
        if (MainActivity.getIsAppInBackground()) terminateAll()
    }

    private fun terminateAll() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun broadcastIntent(action: String): PendingIntent =
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