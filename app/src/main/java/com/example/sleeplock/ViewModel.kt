package com.example.sleeplock

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.subscribeBy


class ViewModel(application: Application) : AndroidViewModel(application) {

    val updateCurrentTime = MutableLiveData<String>() // Tasked with keeping our current time text view up to date
    val itemIndexLD = MutableLiveData<Int>() // rename this
    val enabledDisabled = MutableLiveData<Boolean>() // Tasked with enabling/disabling our button
    val updateButtonColor = MutableLiveData<Int>()
    private val repository = Repository(application)
    lateinit var timer: Timer
    private var isTimeChosen = false
    private var isSoundChosen = false
    var isTimerRunning = false
    var start = true // used for our start/pause button

    private var timeInMillis: Long = 0


    init {
        itemIndex.subscribeBy( // index of item selected in recycler view
            onNext = { index ->
                isSoundChosen = true

                enabledDisabled.value = isTimeAndSoundChosen(isTimeChosen, isSoundChosen)

                sendColor(isTimeAndSoundChosen(isTimeChosen, isSoundChosen))

                // todo show a "sound selected" toast here
                itemIndexLD.value = index
            }
        )


        dialogTime.subscribeBy( // User selected time from our dialog
            onNext = { milliSec ->
                isTimeChosen = true

                enabledDisabled.value = isTimeAndSoundChosen(isTimeChosen, isSoundChosen)
                sendColor(isTimeAndSoundChosen(isTimeChosen, isSoundChosen))

                val formattedTime = milliSec.formatTime()
                updateCurrentTime.value = formattedTime

                timeInMillis = milliSec

            })

    }


    fun startService() {
        repository.startService(timeInMillis)
        isTimerRunning = true
    }

    fun pauseService() = repository.pauseService()

    private fun resetService() = repository.resetService()

    private fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)
        timer.currentTime.subscribeBy(
            onNext = { milliSec ->
                updateCurrentTime.postValue(milliSec.formatTime())
                timeInMillis = milliSec
            },
            onComplete = {
                //todo add a "timer finished" toast with Toasty
                updateCurrentTime.postValue(resetTimeDisplayes())
            }
        )
    }


    fun startTimer() {
        createAndObserveTimer(timeInMillis)
        timer.startTimer()
        start = !start
        isTimerRunning = true
    }

    fun pauseTimer() {
        timer.pauseTimer()
        start = !start
        isTimerRunning = false
    }

    fun resetTimer() {
        timer.resetTimer()
        isTimerRunning = false
    }


    private fun isTimeAndSoundChosen(isTimeChosen: Boolean, isSoundChosen: Boolean): Boolean {
        return isTimeChosen && isSoundChosen // Both must be true to evaluate to true
    }


    private fun sendColor(isTimeAndSoundChosen: Boolean) {
        if (isTimeAndSoundChosen) {
            updateButtonColor.value = Color.parseColor("#4dd0e1")  // light blue
        } else {
            updateButtonColor.value = Color.parseColor("#0B3136") // dark/dull blue
        }
    }

    fun startRunningService() { // Think of a more descriptive name
        // Starts the service only if the timer is running
        if (isTimerRunning) {
            startService() // Only starts the service if the timer is running (wont start if paused)
            timer.resetTimer()
            repository.saveServiceStatus()
        }
    }


    fun startNewTimer() {
        // Destroy's service if running, and creates the foreground timer with the services timer's latest time

        if (isServiceRunning) {
            sendTimeToViewModel.subscribeBy(onNext = { millis ->
                createAndObserveTimer(millis)
            })

            resetService()

            startTimer()
        }
    }


}



