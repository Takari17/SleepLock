package com.example.sleeplock

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import es.dmoral.toasty.Toasty
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


// Todo: add the sound functionality
class ViewModel(application: Application) : AndroidViewModel(application) {

    // Observed by fragment
    val updateCurrentTime = MutableLiveData<String>() // Tasked with keeping our current time text view up to date
    val clickedItemIndex = MutableLiveData<Int>() // index of the item selected from the recycler view
    val enabledOrDisabled = MutableLiveData<Boolean>() // Tasked with enabling/disabling our button
    val updateButtonColor = MutableLiveData<Int>()
    val updateButtonText = MutableLiveData<String>()
    val notifyAnimation = MutableLiveData<Boolean>()

    private val repository = Repository(application)
    private lateinit var timer: Timer
    private var isTimeChosen = false
    private var isSoundChosen = false
    private var isTimerRunning = false
    var startButton = true // used for our startButton/pause button

    private var timeInMillis: Long = 0


    init {
        itemIndex.subscribeBy( // index of item selected in recycler view
            onNext = { index ->
                isSoundChosen = true
                enabledOrDisabled.value = isTimeAndSoundChosen()
                setButtonColor(isTimeAndSoundChosen())

                clickedItemIndex.value = index

                GlobalScope.launch(Dispatchers.Main) { showSoundSelectedToast() }

            }
        )

        repository.passTime.observeForever(observeServiceBroadcast())
    }


    fun subscribeToDialog(dialogTime: BehaviorSubject<Long>) {
        dialogTime.subscribeBy( // User selected time from our dialog
            onNext = { milliSec ->
                isTimeChosen = true

                enabledOrDisabled.value = isTimeAndSoundChosen()
                setButtonColor(isTimeAndSoundChosen())

                val formattedTime = milliSec.formatTime()
                updateCurrentTime.value = formattedTime

                createAndObserveTimer(milliSec)
            })
    }


    private fun startService() {
        repository.startService(timeInMillis)
        isTimerRunning = true
    }

    private fun resetService() = repository.resetService() // will invoke "sendTimeToService"

    private fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)
        timer.currentTime.subscribeBy(
            onNext = { milliSec ->
                updateCurrentTime.postValue(milliSec.formatTime())
                timeInMillis = milliSec
            },
            onComplete = {
                updateCurrentTime.postValue(resetTimeDisplayes())

                // Runs returns result on main thread
                GlobalScope.launch(Dispatchers.Main) {
                    resetButtonClick()
                    showFinishedToast(getApplication())
                }

                notifyAnimation.postValue(true)
            }
        )
    }


    private fun startTimer() {
        timer.startTimer()
        startButton = !startButton
        isTimerRunning = true
    }

    private fun pauseTimer() {
        timer.pauseTimer()
        startButton = !startButton
        isTimerRunning = false
    }

    private fun resetTimer() {
        timer.resetTimer()
        isTimerRunning = false
    }


    private fun isTimeAndSoundChosen(): Boolean = isTimeChosen && isSoundChosen // Both must be true to evaluate to true


    private fun setButtonColor(isTimeAndSoundChosen: Boolean) {
        if (isTimeAndSoundChosen) {
            updateButtonColor.value = Color.parseColor("#4dd0e1")  // light blue
        } else {
            updateButtonColor.value = Color.parseColor("#0B3136") // dark/dull blue
        }
    }

    private fun setButtonText(isButtonStart: Boolean) {
        return if (isButtonStart) {
            updateButtonText.value = "Pause"
        } else {
            updateButtonText.value = "Start"
        }
    }

    private fun setButtonEnabledDisabled(isTimeAndSoundChosen: Boolean) {
        enabledOrDisabled.value = isTimeAndSoundChosen
    }


    // maybe create a higher order function for these 2 methods, if the service is running do this, else do this...

    fun maybeStartService() {
        // Only starts the service if the timer is running (wont startButton if paused)
        if (isTimerRunning) {
            startService()
            timer.resetTimer()
        }
    }

    fun destroyService() {
        // Destroy's service if running, invokes Live Data which creates the foreground timer with the services timer's latest time
        if (isServiceRunning) {
            resetService()
        }
    }

    private fun observeServiceBroadcast(): Observer<Long> {
        return Observer { millis ->
            createAndObserveTimer(millis)
            startTimer()
        }
    }


    fun startButtonClick(startButton: Boolean) {
        startTimer()
        setButtonText(startButton)
    }

    fun pauseButtonClick(startButton: Boolean) {
        pauseTimer()
        setButtonText(startButton)
    }

    fun resetButtonClick() {
        resetTimer()
        resetBooleans()
        setButtonText(false)
        setButtonColor(isTimeAndSoundChosen())
        setButtonEnabledDisabled(isTimeAndSoundChosen())

    }

    private fun resetBooleans() {
        // resets booleans to default state
        isTimeChosen = false
        isSoundChosen = false
        isTimerRunning = false
        startButton = true
    }

    fun restoreButton() {
        setButtonText(true)
        setButtonColor(true)
        setButtonEnabledDisabled(true)
    }

    private fun showSoundSelectedToast() {
        Toasty.success(getApplication(), "Sound Selected", Toasty.LENGTH_SHORT, true).show()
    }

}





