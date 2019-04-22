package com.example.sleeplock.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.R
import com.example.sleeplock.feature.SoundPlayer
import com.example.sleeplock.feature.Timer
import com.example.sleeplock.model.Repository
import com.example.sleeplock.model.service.foregroundTimerPaused
import com.example.sleeplock.model.service.foregroundTimerRunning
import com.example.sleeplock.model.service.isServiceRunning
import com.example.sleeplock.model.service.serviceTimerPaused
import com.example.sleeplock.ui.adapters.itemIndex
import com.example.sleeplock.utils.formatTime
import io.reactivex.rxkotlin.subscribeBy


/*
Commit:

Major design change and bug fixes:

Fixed buggy animations
Fixed a bug where the recycler view click sometimes doesn't register
Updated gradle dependencies
Changed Global broadcast reciever to local broadcast receiver to decrease the time it takes for the Ui to receiver the service time
Fixed a bugg where the tabs would sometimes overlap the recycler view
Fixed  bug where the animationss will run twice on reset
Fixed a bug where the service woudnt update the UI with the latest time
Added package strucuture for better organization
Moved hard coded string delcaations to string resource for language translation
Fixed our buggy toast, now the finished toast only shows from the service
We added a warning toast if the user selects a time while the timer is active
Moved most business logic from Fragment to View model
Fixed a bug where the screen woudnt lock in portrait mode

Created immutable getter methods for our live data so avoid unintended data changes

renambed some methods and variables for better clarity

Created a new package structure for better organization

Created a util class and added my helper methods and constants in there

Some more bug fixes

Added a Resume text for start pause button

 */


/*


todo: add the sound functionality


 todo maybe think about another theme


What if we just create a background service that runs with the sound and timer, and create a foreground service that communicates with that one source

We can use a broadcast receiver for communicating bewteen the services, problem with this is it's gonna have a delay which will make our app seem unresponsive, so idk

Well I am leaning towrds having one service, having 2 sound and timers seems really ineffective and overcomplicated


We need to fins a way to start a foreground service, but toggle whether or not its notification is displayed

 */



class MyViewModel(application: Application) : AndroidViewModel(application) {

    // Observed by Main Fragment
    private val currentTime = MutableLiveData<String>() // keeps current time text view up to date
    private val timerCompleted = MutableLiveData<Boolean>()
    private val clickedItemIndex = MutableLiveData<Int>() // item clicked from recycler view
    private val buttonEnabled = MutableLiveData<Boolean>()
    private val buttonColor = MutableLiveData<Int>()
    private val buttonText = MutableLiveData<String>()

    private val repository = Repository(application)

    private lateinit var timer: Timer
    private lateinit var soundPlayer: SoundPlayer

    private var isTimeChosen = false
    private var isSoundChosen = false
    var startButtonClicked = true // used for switching from start/pause functionality
    var reverseAnim = false
    var timeInMillis: Long = 0


    private val itemSelected = itemIndex.subscribeBy { index ->
        isSoundChosen = true
        buttonEnabled.value = isTimeAndSoundChosen()
        setButtonColor(isTimeAndSoundChosen())

        if (!foregroundTimerRunning) clickedItemIndex.value = index // only updates card view data if the timer isn't running

        createSound(index)
    }


    fun dispose() = itemSelected.dispose()

    fun passDialogTime(millis: Long) { // millis = Minutes expressed as a long
        isTimeChosen = true
        buttonEnabled.value = isTimeAndSoundChosen()
        setButtonColor(isTimeAndSoundChosen())

        val formattedTime = millis.formatTime()
        currentTime.value = formattedTime

        createAndObserveTimer(millis)
    }

    fun getCurrentTime(): LiveData<String> = currentTime

    fun getClickedItemIndex(): LiveData<Int> = clickedItemIndex

    fun getButtonEnabled(): LiveData<Boolean> = buttonEnabled

    fun getButtonColor(): LiveData<Int> = buttonColor

    fun getButtonText(): LiveData<String> = buttonText

    fun getTimerCompleted(): LiveData<Boolean> = timerCompleted


    private fun startService() {
        repository.startService(timeInMillis)
        foregroundTimerRunning = true
    }

    private fun resetService() = repository.resetService()

    fun createAndObserveTimer(millis: Long) {
        timer = Timer(millis)
        timer.currentTime.subscribeBy(
            onNext = { milliSec ->
                currentTime.postValue(milliSec.formatTime())
                timeInMillis = milliSec
            },
            onComplete = {
                currentTime.postValue(getResourceString(R.string.reset_time))
                timerCompleted.postValue(true)
                resetBooleans()
            }
        )
    } fun createSound(index: Int) {
        soundPlayer = SoundPlayer(getApplication(), index)
    }

    fun startTimer() {// todo: how about we call the set button text methods here?
        timer.startTimer()
        soundPlayer.startMediaPlayer()
        startButtonClicked = false
        foregroundTimerRunning = true
        foregroundTimerPaused = false
    }

    fun pauseTimer() {
        timer.pauseTimer()
        soundPlayer.pauseMediaPlayer()
        startButtonClicked = true
        foregroundTimerPaused = true


    }

    private fun resetTimer() {
        timer.resetTimer()
        soundPlayer.resetMediaPlayer()
        foregroundTimerRunning = false
        foregroundTimerPaused = false
    }

    private fun isTimeAndSoundChosen(): Boolean = isTimeChosen && isSoundChosen // Both must be true to evaluate to true

    private fun setButtonColor(isTimeAndSoundChosen: Boolean) {
        if (isTimeAndSoundChosen) {
            buttonColor.value = Color.parseColor("#4dd0e1")  // light blue
        } else {
            buttonColor.value = Color.parseColor("#0B3136") // dark/dull blue
        }
    }

    private fun setButtonText(isButtonStart: Boolean) {
        return if (isButtonStart) {
            buttonText.value = getResourceString(R.string.pause)
        } else {
            buttonText.value = getResourceString(R.string.resume)
        }
    }

    private fun setButtonEnabledDisabled(isTimeAndSoundChosen: Boolean) {
        buttonEnabled.value = isTimeAndSoundChosen
    }

    fun maybeStartService() {
        // Only starts the service if the timer is running (wont start if paused)
        if (foregroundTimerRunning) {

            if (!foregroundTimerPaused) startService()
        }
    }

    fun destroyService() {
        // Destroy's service if running and sends the time to the foreground timer

        if (isServiceRunning) {
            resetService()
            restoreState()
        }
    }

    fun startPauseButtonClick(start: Boolean) {
        if (start) startButtonClick() else pauseButtonClick()
    }

    private fun startButtonClick() {
        startTimer()
        setButtonText(true)
    }

    private fun pauseButtonClick() {
        pauseTimer()
        setButtonText(false)
    }

    fun resetButtonClick() {
        // Invokes timerCompleted Live Data
        resetTimer()
        resetBooleans()
        setButtonColor(isTimeAndSoundChosen())
        setButtonEnabledDisabled(isTimeAndSoundChosen())
    }

    private fun resetBooleans() {
        // resets booleans to default state
        isTimeChosen = false
        isSoundChosen = false
        foregroundTimerRunning = false
        foregroundTimerPaused = false
        startButtonClicked = true
    }

    fun resetButton() {
        buttonText.value = getResourceString(R.string.start) // todo want to create a method that handles it
//        setButtonText(false)
        setButtonColor(false)
        setButtonEnabledDisabled(false)
    }

    private fun restoreState() {
        if (serviceTimerPaused) setButtonText(false) else setButtonText(true)

        setButtonColor(true)
        setButtonEnabledDisabled(true)
        isSoundChosen = true
        isTimeChosen = true
    }

    private fun getResourceString(id: Int): String {
        val resources = getApplication<Application>().resources
        return resources.getString(id)
    }
}