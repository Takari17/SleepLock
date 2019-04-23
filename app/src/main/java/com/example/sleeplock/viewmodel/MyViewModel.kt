package com.example.sleeplock.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.R
import com.example.sleeplock.feature.isTimerPaused
import com.example.sleeplock.feature.isTimerRunning
import com.example.sleeplock.model.Repository
import com.example.sleeplock.model.isServiceRunning
import com.example.sleeplock.ui.itemIndex
import com.example.sleeplock.utils.getResourceString
import io.reactivex.rxkotlin.subscribeBy

class MyViewModel(application: Application) : AndroidViewModel(application) {

    // Observed by Main Fragment
    private val clickedItemIndex = MutableLiveData<Int>() // item clicked from recycler view
    private val buttonEnabled = MutableLiveData<Boolean>()
    private val buttonColor = MutableLiveData<Int>()
    private val buttonText = MutableLiveData<String>()


    fun getClickedItemIndex(): LiveData<Int> = clickedItemIndex
    fun getButtonEnabled(): LiveData<Boolean> = buttonEnabled
    fun getButtonColor(): LiveData<Int> = buttonColor
    fun getButtonText(): LiveData<String> = buttonText

    // From repository

    fun getCurrentTime() = repository.getCurrentTime()
    fun getTimerStarted() = repository.getTimerStarted()
    fun getTimerPaused() = repository.getTimerPaused()
    fun getTimerCompleted() = repository.getTimerCompleted()


    private val repository = Repository(application)

    private var isTimeChosen = false
    private var isSoundChosen = false
    var startButtonClicked = true // used for switching from start/pause functionality
    var reverseAnim = false

    // Passed during run time
    var millis: Long? = null
    var index: Int? = null


    private val itemSelected = itemIndex.subscribeBy { index ->

        this.index = index

        isSoundChosen = true

        buttonEnabled.value = isTimeAndSoundChosen()
        setButtonColor(isTimeAndSoundChosen())

        if (!isTimerRunning) clickedItemIndex.value = index // only updates card view data if the timer isn't running
    }


    fun dispose() = itemSelected.dispose()

    fun passDialogTime(millis: Long) {

        this.millis = millis

        isTimeChosen = true

        buttonEnabled.value = isTimeAndSoundChosen()
        setButtonColor(isTimeAndSoundChosen())
    }

    private fun startSoundAndTimer() {
        // Only called if millis & index  != null
        repository.startSoundAndTimer(millis!!, index!!)
        startButtonClicked = false
    }

    private fun pauseSoundAndTimer() {
        repository.pauseSoundAndTimer()
        startButtonClicked = true
    }

    private fun resumeSoundAndTimer() {
        repository.resumeSoundAndTimer()
        startButtonClicked = false
    }

    private fun resetSoundAndTimer() {
        // Invokes the timerCompleted Live Data
        repository.resetSoundAndTimer()
    }

    fun bindToService() = repository.bindToService()

    private fun isTimeAndSoundChosen(): Boolean = isTimeChosen && isSoundChosen // Both must be true to evaluate to true

    private fun setButtonColor(isTimeAndSoundChosen: Boolean) {
        if (isTimeAndSoundChosen) {
            buttonColor.value = Color.parseColor("#4dd0e1")  // light blue
        } else {
            buttonColor.value = Color.parseColor("#0B3136") // dark/dull blue
        }
    }

    fun setButtonText(isButtonStart: Boolean) {
        return if (isButtonStart) {
            buttonText.value = getApplication<Application>().getResourceString(R.string.pause)
        } else {
            buttonText.value = getApplication<Application>().getResourceString(R.string.resume)
        }
    }


    fun startPauseButtonClick(start: Boolean) {
        if (start) startButtonClick() else pauseButtonClick()
    }


    private fun startButtonClick() {
        if (isServiceRunning) resumeSoundAndTimer() else startSoundAndTimer()
        setButtonText(true)
    }

    private fun pauseButtonClick() {
        pauseSoundAndTimer()
        setButtonText(false)
    }

    fun resetButtonClick() {
        resetSoundAndTimer()
    }

    fun resetAll() {
        resetBooleans()
        resetButton()
    }

    private fun resetBooleans() {
        // resets booleans to default state
        isTimeChosen = false
        isSoundChosen = false
        startButtonClicked = true
    }

    private fun resetButton() {
        buttonText.value = getApplication<Application>().getResourceString(R.string.start)
        setButtonColor(false)
        buttonEnabled.value = false
    }

    fun restoreState() {
        startButtonClicked = if (isTimerPaused) {
            setButtonText(false)
            true
        } else {
            setButtonText(true)
            false
        }

        setButtonColor(true)

        buttonEnabled.value = true

        isSoundChosen = true
        isTimeChosen = true

        clickedItemIndex.value = index
    }
}