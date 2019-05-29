package com.example.sleeplock.ui.viewmodel

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sleeplock.R
import com.example.sleeplock.data.Repository
import com.example.sleeplock.ui.common.Animate
import com.example.sleeplock.utils.*
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/*
 * Shared by MainFragment and ListFragment, however this View Model primarily references the MainFragments properties. ListFragment
 * didn't have enough logic to warrant it's own View Model and it also needs to communicate with MainFragment so I made them share a
 * this View Model.
 */
class MainViewModel @Inject constructor(
    private val context: Context,
    private val repository: Repository
) : ViewModel() {


    private val buttonEnabled = MutableLiveData<Boolean>()
    private val buttonColor = MutableLiveData<Int?>()
    private val buttonText = MutableLiveData<String?>()
    private val cardViewImage = MutableLiveData<Int?>(R.drawable.nosound)
    private val cardViewText = MutableLiveData<String?>(getResourceString(R.string.no_sound))
    private val startAnimation = MutableLiveData<Long>()
    private val reverseAnimation = MutableLiveData<Boolean>()

    val currentTime = repository.getCurrentTime()
    val isTimerRunning = repository.getIsTimerRunning()
    private val isTimerCompleted = repository.isTimerCompleted


    // Only exposes immutable Live Data
    fun getButtonEnabled(): LiveData<Boolean> = buttonEnabled

    fun getButtonColor(): LiveData<Int?> = buttonColor
    fun getButtonText(): LiveData<String?> = buttonText
    fun getCardViewImage(): LiveData<Int?> = cardViewImage
    fun getCardViewText(): LiveData<String?> = cardViewText
    fun getStartAnimation(): LiveData<Long> = startAnimation
    fun getReverseAnimation(): LiveData<Boolean> = reverseAnimation
    fun getDidTimerStart() = repository.wasTimerStarted

    private val compositeDisposable = CompositeDisposable()

    private val isTimeChosen = BehaviorRelay.createDefault(false)
    private val isSoundChosen = BehaviorRelay.createDefault(false)

    private val sharedPrefs = repository.sharedPrefs

    // Passed during run time
    private var millis: Long? = null
    private var index: Int? = null

    init {
        restoreDataIfTimerRunning()
    }

    /*
    If the time and sound are chosen the start button will turn light blue and will be enabled, else it will turn dull/dark blue
    and will be disabled until the user selects both a time and a sound.
    */
    init {
        compositeDisposable += Observables
            .combineLatest(isTimeChosen, isSoundChosen) { timeChosen, soundChosen ->
                if (timeChosen && soundChosen) {
                    setButtonEnabled(true)
                    setButtonColor(true)
                } else {
                    setButtonEnabled(false)
                    setButtonColor(false)
                }
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    init {
        compositeDisposable += isTimerCompleted
            .subscribeBy(
                onNext = {
                    resetAll()
                    reverseAnimation.postValue(true)
                },
                onError = { Log.d("zwi", "Error observing isTimerCompleted in view model: $it") }
            )
    }

    fun startPauseButtonClick(isTimerRunning: Boolean) =
        if (!isTimerRunning) startButtonClick(getDidTimerStart()) else pauseButtonClick()


    private fun startButtonClick(isTimerStarted: Boolean) =
        if (isTimerStarted) resumeSoundAndTimer()
        else {
            startSoundAndTimer()
            startAnimation.value = Animate.DEFAULT
        }


    private fun pauseButtonClick() =
        pauseSoundAndTimer()


    //Invokes resetAll(), everything will return to it's default state when this is called.
    fun resetButtonClick() =
        resetSoundAndTimer()


    private fun resetAll() {
        resetBooleans()
        resetButton()
        resetCardViewData()
    }

    //Resets booleans to their default state.
    private fun resetBooleans() {
        isTimeChosen.accept(false)
        isSoundChosen.accept(false)
        millis = null
        index = null
    }

    private fun resetButton() {
        buttonText.postValue(getResourceString(R.string.start))
        isSoundChosen.accept(false)
        isTimeChosen.accept(false)
    }

    fun passDialogTime(millis: Long) {
        this.millis = millis
        isTimeChosen.accept(true)
    }

    fun subscribeToItemIndex(itemIndex: BehaviorRelay<Int>) {
        compositeDisposable += itemIndex
            .subscribeBy(
                onNext = { index ->

                    this.index = index

                    isSoundChosen.accept(true)

                    // Will only update card view data if the timer is NOT running.
                    if (isTimerRunning.value != true) setCardViewData(index)
                },
                onError = { Log.d("zwi", "Error observing item index in view model: $it") }
            )
    }

    //I took precautions to ensure index and millis aren't null by this point. (the start button will be disabled)
    private fun startSoundAndTimer() =
        repository.startSoundAndTimer(millis!!, index!!)


    private fun pauseSoundAndTimer() =
        repository.pauseSoundAndTimer()


    private fun resumeSoundAndTimer() =
        repository.resumeSoundAndTimer()


    private fun resetSoundAndTimer() =
        repository.resetSoundAndTimer()


    private fun setButtonColor(isTimeAndSoundChosen: Boolean) =
        if (isTimeAndSoundChosen) buttonColor.postValue(Color.parseColor("#4dd0e1")) // light blue
        else buttonColor.postValue(Color.parseColor("#0B3136")) // dark/dull blue


    private fun setButtonEnabled(isTimeAndSoundChosen: Boolean) =
        if (isTimeAndSoundChosen) buttonEnabled.postValue(true)
        else buttonEnabled.postValue(false)


    fun setButtonText(isTimerRunning: Boolean) =
        if (isTimerRunning) buttonText.value = getResourceString(R.string.pause)
        else buttonText.value = getResourceString(R.string.resume)


    private fun setCardViewData(index: Int) {
        cardViewImage.postValue(ITEM_PIC[index])
        cardViewText.postValue(ITEM_TEXT[index])
    }

    private fun resetCardViewData() {
        cardViewImage.postValue(R.drawable.nosound)
        cardViewText.postValue(getResourceString(R.string.no_sound))
    }


    private fun saveDataIfTimerRunning() {
        if (isTimerRunning.value == true) sharedPrefs.apply {

            saveBooleanIfNonNull(BUTTON_ENABLED, buttonEnabled.value)

            saveIntIfNonNull(BUTTON_COLOR, buttonColor.value)

            saveStringIfNonNull(BUTTON_TEXT, buttonText.value)

            saveIntIfNonNull(CARD_VIEW_IMAGE, cardViewImage.value)

            saveStringIfNonNull(CARD_VIEW_TEXT, cardViewText.value)

            saveBooleanIfNonNull(IS_TIME_CHOSEN, isTimeChosen.value)

            saveBooleanIfNonNull(IS_SOUND_CHOSEN, isSoundChosen.value)
        }
    }

    private fun restoreDataIfTimerRunning() {
        if (isTimerRunning.value == true) {

            buttonEnabled.value = sharedPrefs.getBoolean(BUTTON_ENABLED, false)

            buttonColor.value = sharedPrefs.getInt(BUTTON_COLOR)

            buttonText.value = sharedPrefs.getString(BUTTON_TEXT)

            cardViewImage.value = sharedPrefs.getInt(CARD_VIEW_IMAGE)

            cardViewText.value = sharedPrefs.getString(CARD_VIEW_TEXT)

            isTimeChosen.accept(sharedPrefs.getBoolean(IS_TIME_CHOSEN, false))

            isSoundChosen.accept(sharedPrefs.getBoolean(IS_SOUND_CHOSEN, false))
        }
    }

    private fun bindToServiceIfRunning() = repository.bindToServiceIfRunning()

    private fun unbindFromServiceIfRunning() = repository.unbindFromServiceIfRunning()

    private fun removeServiceLiveDataSources() = repository.removeServiceLiveDataSources()

    private fun getResourceString(id: Int): String = context.resources.getString(id)

    fun mainFragmentOnStart() {
        bindToServiceIfRunning()
    }

    fun mainFragmentOnStop() {
        saveDataIfTimerRunning()
        unbindFromServiceIfRunning()
        removeServiceLiveDataSources()
    }

    override fun onCleared() {
        super.onCleared()
        repository.clearDisposables()
        compositeDisposable.clear()
    }
}