package com.example.sleeplock.data.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.*
import com.example.sleeplock.Application.Companion.applicationComponent
import com.example.sleeplock.R
import com.example.sleeplock.data.features.Timer
import com.example.sleeplock.data.features.WhiteNoise
import com.example.sleeplock.data.receiver.NotificationBroadcastReceiver
import com.example.sleeplock.ui.MainActivity
import com.example.sleeplock.ui.isAppInBackground
import com.example.sleeplock.utils.*
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/*
 * Holds a Timer and WhiteNoise instance for the Repository to observe
 */
class MainService : LifecycleService() {

    companion object {
        /*Only modified within this service. Set to true in onCreate and set to false in onDestroy. */
        var isRunning = false
    }

    private val sharedPrefs = applicationComponent.sharedPrefs

    private lateinit var timer: Timer
    private lateinit var whiteNoise: WhiteNoise

    private val compositeDisposable = CompositeDisposable()

    private var isTimerAndSoundCreated = false
    private var playOrPause = 0

    private val currentTime = MutableLiveData<Long>()
    private val wasTimerStarted = BehaviorRelay.createDefault(false)
    private val isTimerRunning = MediatorLiveData<Boolean>()
    val isTimerCompleted = PublishRelay.create<Boolean>()

    private val showCompletedToast = MutableLiveData<Boolean>()

    override fun onCreate() {
        super.onCreate()
        isRunning = true
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return LocalBinder()
    }

    init {
        showCompletedToast.observe(this, Observer { showFinishedToast(this) })
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // Prevents duplication on sound & timer
        if (!isTimerAndSoundCreated) {

            //We ensure'd that no null values are sent to the service, however if they are then something went HORRIBLY wrong and it's better to just crash the app.
            val millis: Long = intent.extras?.getLong(MILLIS)!!
            val index: Int = intent.extras?.getInt(INDEX)!!

            createAndObserveTimer(millis)
            createSound(index)
            isTimerAndSoundCreated = true
        }

        when (intent.action) {
            IntentAction.PLAY.name -> {
                startSoundAndTimer()
                playOrPause = 1
            }
            IntentAction.PAUSE.name -> {
                pauseSoundAndTimer()
                playOrPause = 0
            }
            IntentAction.RESET.name -> {
                resetSoundAndTimer() // will trigger reset all
                return START_NOT_STICKY
            }
        }

        val timeString: String? = currentTime.value?.formatTime()

        startForeground(NOTIFICATION_ID, getForegroundNotification(timeString ?: "00:00", playOrPause))
        return START_STICKY
    }

    // Updates the notification content description with the text provided
    private fun updateNotification(text: String, playOrPause: Int) =
        NotificationManagerCompat.from(this).apply {

            notify(NOTIFICATION_ID, getForegroundNotification(text, playOrPause))
        }


    private fun getForegroundNotification(newText: String, playOrPause: Int): Notification {

        // I can just use the pending intent methods in place of these variable usages but this style is more readable to me
        val contentIntent = createActivityPendingIntent()
        val pendingPlayIntent = createBroadcastPendingIntent(IntentAction.PLAY.name)
        val pendingPauseIntent = createBroadcastPendingIntent(IntentAction.PAUSE.name)
        val pendingResetIntent = createBroadcastPendingIntent(IntentAction.RESET.name)

        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.alarm_icon)
            addAction(R.drawable.play, getResourceString(this@MainService, R.string.start), pendingPlayIntent)
            addAction(R.drawable.pause, getResourceString(this@MainService, R.string.pause), pendingPauseIntent)
            addAction(R.drawable.reset, getResourceString(this@MainService, R.string.reset), pendingResetIntent)
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(playOrPause, 2)  // index of the actions
            )
            setSubText(getResourceString(this@MainService, R.string.subText))
            setContentTitle(getResourceString(this@MainService, R.string.contentTitle))
            setContentText(newText)
            setContentIntent(contentIntent)
        }.build()
    }


    private fun createSound(index: Int) {
        whiteNoise = WhiteNoise(MediaPlayer(), this, index)
    }


    private fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis).apply {

            compositeDisposable += currentTime
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = { timeInMilli ->
                        this@MainService.currentTime.postValue(timeInMilli)
                        updateNotification(timeInMilli.formatTime(), playOrPause)
                    },
                    onError = { Log.d("zwi", "Error observing currentTime in MainService: $it") }
                )

            compositeDisposable += isTimerCompleted
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = { completed ->
                        this@MainService.currentTime.postValue(0)
                        this@MainService.isTimerCompleted.accept(completed)
                        showCompletedToast.postValue(completed)
                        whiteNoise.reset()
                        resetAll()
                    },
                    onError = { Log.d("zwi", "Error observing isTimerCompleted in MainService: $it") }
                )

            compositeDisposable += wasTimerStarted
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = { wasStarted -> this@MainService.wasTimerStarted.accept(wasStarted) },
                    onError = { Log.d("zwi", "Error observing wasTimerStarted in MainService: $it") }
                )

            isTimerRunning.addSource(getIsTimerRunning()) { isRunning ->
                isTimerRunning.value = isRunning
            }
        }
    }

    private fun startSoundAndTimer() {
        whiteNoise.start()
        timer.start()
    }

    fun pauseSoundAndTimer() {
        whiteNoise.pause()
        timer.pause()
    }

    fun resumeSoundAndTimer() {
        whiteNoise.start()
        timer.resume()
    }

    //Sound is reset on timer complete
    fun resetSoundAndTimer() = timer.reset()


    private fun resetAll() {
        sharedPrefs.resetAllData()
        isTimerAndSoundCreated = false

        stopSelf()
        stopForeground(true)

        if (isAppInBackground) terminateAll()
    }

    // Only exposes immutable Live Data properties
    fun getCurrentTime(): LiveData<Long> = currentTime

    fun getWasTimerStarted(): BehaviorRelay<Boolean> = wasTimerStarted

    fun getIsTimerRunning(): LiveData<Boolean> = isTimerRunning


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
            NotificationBroadcastReceiver.createIntent(this, action),
            0
        )


    private fun terminateAll() = android.os.Process.killProcess(android.os.Process.myPid())

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        isTimerRunning.removeSource(timer.getIsTimerRunning())
        isRunning = false
    }

    inner class LocalBinder : Binder() {
        fun getService(): MainService = this@MainService
    }
}