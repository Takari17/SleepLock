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

/*Only modified within this service. Set to true in onCreate and set to false in onDestroy.
*
* */
var isMainServiceRunning = false

/*
 * This service holds the Timer and WhiteNoise class and exposes timer callbacks and other
 * related data so the Repository can observe the data.
 */
class MainService : LifecycleService() {

    private val injector = applicationComponent

    private val sharedPrefs = injector.sharedPrefs

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
        isMainServiceRunning = true
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

            //We ensure'd that no null values are sent to the service, but if they are then something went HORRIBLY wrong and it's better to crash the app.
            val millis: Long = intent.extras?.getLong(MILLIS)!!
            val index: Int = intent.extras?.getInt(INDEX)!!

            createAndObserveTimer(millis)
            createSound(index)
            isTimerAndSoundCreated = true
        }

        when (intent.action) {
            ACTION_PLAY -> {
                startSoundAndTimer()
                playOrPause = 1
            }
            ACTION_PAUSE -> {
                pauseSoundAndTimer()
                playOrPause = 0
            }
            ACTION_RESET -> {
                resetSoundAndTimer() // will trigger reset all
                return START_NOT_STICKY
            }
        }

        val time: String? = currentTime.value?.formatTime()

        startForeground(NOTIFICATION_ID, getForegroundNotification(time ?: "00:00", playOrPause))
        return START_STICKY
    }

    // Updates the notification content description with the text provided
    private fun updateNotification(text: String, playOrPause: Int) =
        NotificationManagerCompat.from(this).apply {

            notify(NOTIFICATION_ID, getForegroundNotification(text, playOrPause))
        }


    private fun getForegroundNotification(newText: String, playOrPause: Int): Notification {

        val contentIntent = createActivityPendingIntent()
        val pendingPlayIntent = createBroadcastPendingIntent(ACTION_PLAY)
        val pendingPauseIntent = createBroadcastPendingIntent(ACTION_PAUSE)
        val pendingResetIntent = createBroadcastPendingIntent(ACTION_RESET)

        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.alarm_icon)
            addAction(R.drawable.play, getResourceString(R.string.start), pendingPlayIntent)
            addAction(R.drawable.pause, getResourceString(R.string.pause), pendingPauseIntent)
            addAction(R.drawable.reset, getResourceString(R.string.reset), pendingResetIntent)
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(playOrPause, 2)  // index of the actions
            )
            setSubText(getResourceString(R.string.subText))
            setContentTitle(getResourceString(R.string.contentTitle))
            setContentText(newText)
            setContentIntent(contentIntent)
        }.build()
    }


    private fun createSound(index: Int) {
        whiteNoise = WhiteNoise(MediaPlayer(), this, index)
    }


    private fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)

        compositeDisposable += timer.currentTime
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { timeInMilli ->
                    currentTime.postValue(timeInMilli)
                    updateNotification(timeInMilli.formatTime(), playOrPause)
                },
                onError = { Log.d("zwi", "Error observing currentTime in MainService: $it") }
            )

        compositeDisposable += timer.isTimerCompleted
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { completed ->
                    showCompletedToast.postValue(completed)
                    currentTime.postValue(0)
                    isTimerCompleted.accept(completed)
                    whiteNoise.reset()
                    resetAll()
                },
                onError = { Log.d("zwi", "Error observing isTimerCompleted in MainService: $it") }
            )

        compositeDisposable += timer.wasTimerStarted
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { wasStarted -> wasTimerStarted.accept(wasStarted) },
                onError = { Log.d("zwi", "Error observing wasTimerStarted in MainService: $it") }
            )

        isTimerRunning.addSource(timer.getIsTimerRunning()) { isRunning ->
            isTimerRunning.value = isRunning
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

        if (isAppInBackground) terminateAll() // will terminate app if it's in the background
    }

    // Only exposes immutable Live Data properties
    fun getCurrentTime(): LiveData<Long> = currentTime

    fun getWasTimerStarted(): BehaviorRelay<Boolean> = wasTimerStarted

    fun getIsTimerRunning(): LiveData<Boolean> = isTimerRunning


    private fun createActivityPendingIntent(): PendingIntent =
        Intent(this, MainActivity::class.java).let { activityIntent ->
            PendingIntent.getActivity(
                this, 1, activityIntent, 0
            )
        }

    private fun createBroadcastPendingIntent(action: String): PendingIntent =
        Intent(this, NotificationBroadcastReceiver::class.java).apply {
            this.action = action
        }.let { broadcastIntent ->
            PendingIntent.getBroadcast(this, 0, broadcastIntent, 0)
        }

    private fun terminateAll() = android.os.Process.killProcess(android.os.Process.myPid())

    private fun getResourceString(id: Int): String = resources.getString(id)

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        isTimerRunning.removeSource(timer.getIsTimerRunning())
        isMainServiceRunning = false
    }

    inner class LocalBinder : Binder() {
        fun getService(): MainService = this@MainService
    }
}