package com.example.sleeplock.ui.viewmodel

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sleeplock.R
import com.example.sleeplock.data.Repository
import com.example.sleeplock.data.features.isTimerPaused
import com.example.sleeplock.data.features.isTimerRunning
import com.example.sleeplock.utils.getResourceString
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val context: Context,
    private val repository: Repository
) : ViewModel() {

    // Observed by Main Fragment
    private val clickedItemIndex = MutableLiveData<Int>() // item clicked from recycler view
    private val buttonEnabled = MutableLiveData<Boolean>()
    private val buttonColor = MutableLiveData<Int>()
    private val buttonText = MutableLiveData<String>()
    private val startAnimation = MutableLiveData<Long>() //todo want a better name for this


    fun getClickedItemIndex(): LiveData<Int> = clickedItemIndex
    fun getButtonEnabled(): LiveData<Boolean> = buttonEnabled
    fun getButtonColor(): LiveData<Int> = buttonColor
    fun getButtonText(): LiveData<String> = buttonText
    fun getStartAnimation(): LiveData<Long> = startAnimation

    // From repository
    fun getCurrentTime() = repository.getCurrentTime()

    fun getTimerStarted() = repository.getTimerStarted()
    fun getTimerPaused() = repository.getTimerPaused()
    fun getTimerCompleted() = repository.getTimerCompleted()

    private val compositeDisposable = CompositeDisposable()

    private val isTimeChosen = BehaviorRelay.createDefault(false)
    private val isSoundChosen = BehaviorRelay.createDefault(false)

    /*
    If the time and sound are chosen the start button will turn light blue and will be enabled(clickable), else it will turn dull/dark blue
    and will be disabled until the user selects both a time and a sound
     */
    private fun isTimeAndSoundChosen() {
        compositeDisposable += Observables.combineLatest(isTimeChosen, isSoundChosen) { timeChosen, soundChosen ->
            if (timeChosen && soundChosen) {
                buttonEnabled.postValue(true)
                setButtonColor(true)
            } else {
                buttonEnabled.postValue(false)
                setButtonColor(false)
            }
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }


    var startButtonClicked = true // used for switching from start/pause functionality
    var reverseAnim = false

    // Passed during run time
    var millis: Long? = null
    var index: Int? = null

    init {
        isTimeAndSoundChosen()
    }

    fun subscribeToItemIndex(itemIndex: BehaviorRelay<Int>) {
        compositeDisposable += itemIndex
            .subscribeBy(
                onNext = { index ->
                    this.index = index
                    isSoundChosen.accept(true)

                    if (!isTimerRunning) clickedItemIndex.value =
                        index // only updates card view data if the timer isn't running
                },
                onError = { Log.d("zwi", "Error in: $it")}
            )
    }


    fun passDialogTime(millis: Long) {
        this.millis = millis
        isTimeChosen.accept(true)
    }

    private fun startSoundAndTimer() {
        //The start button is disabled until the user selects an index and millis
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

    fun bindToServiceIfRunning() = repository.bindToServiceIfRunning()

    private fun setButtonColor(isTimeAndSoundChosen: Boolean) {
        if (isTimeAndSoundChosen) {
            buttonColor.postValue(Color.parseColor("#4dd0e1"))  // light blue
        } else {
            buttonColor.postValue(Color.parseColor("#0B3136")) // dark/dull blue
        }
    }

    fun setButtonText(isButtonStart: Boolean) {
        return if (isButtonStart) {
            buttonText.value = context.getResourceString(R.string.pause)
        } else {
            buttonText.value = context.getResourceString(R.string.resume)
        }
    }


    fun startPauseButtonClick(start: Boolean) {
        if (start) startButtonClick() else pauseButtonClick()
    }


    private fun startButtonClick() {
        if (repository.isServiceRunning) resumeSoundAndTimer() else startSoundAndTimer()
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
        millis = null
        index = null
    }

    private fun resetBooleans() {
        // resets booleans to default state
        isTimeChosen.accept(false)
        isSoundChosen.accept(false)
        startButtonClicked = true
    }

    private fun resetButton() {
        buttonText.value = context.getResourceString(R.string.start)
        isSoundChosen.accept(false)
        isTimeChosen.accept(false)
    }

    private fun restoreState() {
        startButtonClicked = if (isTimerPaused) {
            setButtonText(false)
            true
        } else {
            setButtonText(true)
            false
        }

        setButtonColor(true)

        isSoundChosen.accept(true)
        isTimeChosen.accept(true)

        clickedItemIndex.value = index
    }

    fun fragmentActivityCreated(){
        if (repository.isServiceRunning) startAnimation.value = 0
    }

    fun onFragmentStart(){
       if (repository.isServiceRunning) restoreState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}