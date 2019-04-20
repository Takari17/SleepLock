package com.example.sleeplock.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.R
import com.example.sleeplock.model.Repository
import com.example.sleeplock.feature.Timer
import com.example.sleeplock.model.service.isServiceRunning
import com.example.sleeplock.model.util.formatTime
import com.example.sleeplock.model.util.showFinishedToast
import com.example.sleeplock.view.itemIndex
import es.dmoral.toasty.Toasty
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/*
Commit:

Fixed buggy animations
Fixed a bug where the recycler view click sometimes doesn't register
Updated gradle dependencies
Changed Global broadcast reciever to local broadcast receiver to decrease the time it takes for the Ui to receiver the service time
Fixed a bugg where the tabs would sometimes overlap the recycler view
Fixed  bug where the animationss will run twice on reset
Fixed a bug where the service woudnt update the UI with the latest time
Added package strucuture for better organization
Moved hard coded string delcaations to string resource for language translation
 */



/*
todo: maybe see if you can bind to the service and use live data instead, create a spearate git branch and test a feature on that
todo fix your toast
todo increase notification buttons
todo: when we click on the notification our activity doesn't open
todo: maybe add a progress bar if we cant increase the time
todo: add the sound functionality
todo what the hell is sp


 */
class MyViewModel(application: Application) : AndroidViewModel(application) {

    // Observed by fragment
    val updateCurrentTime = MutableLiveData<String>() // keeps current time text view up to date
    val clickedItemIndex = MutableLiveData<Int>()
    val enabledOrDisabled = MutableLiveData<Boolean>() // For buttons
    val updateButtonColor = MutableLiveData<Int>()
    val updateButtonText = MutableLiveData<String>()
    val timerCompleted = MutableLiveData<Boolean>()

    private val repository = Repository(application)
    private lateinit var timer: Timer
    private var isTimeChosen = false
    private var isSoundChosen = false
    private var isTimerRunning = false
    var startButton = true // used for our startButton/pause button
    var timeInMillis:Long = 0
    var reverseAnim = false





    init {
        // index of item selected in recycler view
        itemIndex.subscribeBy(onNext = { index ->
                isSoundChosen = true
                enabledOrDisabled.value = isTimeAndSoundChosen()
                setButtonColor(isTimeAndSoundChosen())
                clickedItemIndex.value = index
                GlobalScope.launch(Dispatchers.Main) { showSoundSelectedToast() }
            }
        )
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

    private fun resetService() = repository.resetService()

    fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)
        timer.currentTime.subscribeBy(
            onNext = { milliSec ->
                updateCurrentTime.postValue(milliSec.formatTime())

                timeInMillis = milliSec
            },
            onComplete = {
                updateCurrentTime.postValue(R.string.reset_time.toString())

                // Runs returns result on main thread
                GlobalScope.launch(Dispatchers.Main) {
                    resetButtonClick()
                    showFinishedToast(getApplication())
                }

                timerCompleted.postValue(true)
            }
        )
    }

    fun startTimer() {
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
            updateButtonText.value = R.string.pause.toString()
        } else {
            updateButtonText.value = R.string.start.toString()
        }
    }

    private fun setButtonEnabledDisabled(isTimeAndSoundChosen: Boolean) {
        enabledOrDisabled.value = isTimeAndSoundChosen
    }


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
        isTimeChosen   = false
        isSoundChosen  = false
        isTimerRunning = false
        startButton    = true
    }

    fun restoreButton() {
        setButtonText(true)
        setButtonColor(true)
        setButtonEnabledDisabled(true)
    }

    private fun showSoundSelectedToast() {
        Toasty.success(getApplication(), R.string.sound_selected, Toasty.LENGTH_SHORT, true).show()
    }


}





