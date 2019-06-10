package com.takari.sleeplock.ui.feature.timer

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.takari.sleeplock.R
import com.takari.sleeplock.data.Repository
import com.takari.sleeplock.ui.common.Animate
import com.takari.sleeplock.utils.*
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/*
 * Shared by TimerFragment and WhiteNoiseFragment, however this View Model primarily references TimerFragment's properties. WhiteNoiseFragment
 * doesn't have enough logic to warrant it's own View Model and it also needs to communicate with TimerFragment so I made them share a
 * this View Model instead.
 */
class TimerViewModel @Inject constructor(
    private val context: Context,
    private val repository: Repository
) : ViewModel() {

    //Observed by TimerFragment
    private val buttonEnabled = MutableLiveData<Boolean>()
    private val buttonColor = MutableLiveData<Int?>()
    private val buttonText = MutableLiveData<String?>()
    private val cardViewImage = MutableLiveData<Int?>(R.drawable.nosound)
    private val cardViewText = MutableLiveData<String?>(getResourceString(context, R.string.no_sound))
    private val startAnimation = MutableLiveData<Long>()
    private val reverseAnimation = MutableLiveData<Boolean>()

    val currentTime = repository.getCurrentTime()
    val isTimerRunning = repository.getIsTimerRunning()
    private val isTimerCompleted = repository.isTimerCompleted

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

    //Emits when the timer in MainService.kt completes
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

    // Only exposes immutable Live Data
    fun getButtonEnabled(): LiveData<Boolean> = buttonEnabled

    fun getButtonColor(): LiveData<Int?> = buttonColor

    fun getButtonText(): LiveData<String?> = buttonText

    fun getCardViewImage(): LiveData<Int?> = cardViewImage

    fun getCardViewText(): LiveData<String?> = cardViewText

    fun getStartAnimation(): LiveData<Long> = startAnimation

    fun getReverseAnimation(): LiveData<Boolean> = reverseAnimation

    fun getDidTimerStart() = repository.wasTimerStarted


    fun startPauseButtonClick(isTimerRunning: Boolean) =
        if (!isTimerRunning) startButtonClick(getDidTimerStart()) else pauseButtonClick()


    private fun startButtonClick(wasTimerStarted: Boolean) =
        if (wasTimerStarted) resumeSoundAndTimer()
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
        buttonText.postValue(getResourceString(context, R.string.start))
        isSoundChosen.accept(false)
        isTimeChosen.accept(false)
    }

    fun passDialogTime(millis: Long) {
        this.millis = millis
        isTimeChosen.accept(true)
    }

    //Observes recycler view onClickListener from WhiteNoiseFragment.ktment.kt
    fun subscribeToItemIndex(recyclerViewOnClick: BehaviorRelay<Int>) {
        compositeDisposable += recyclerViewOnClick
            .subscribeBy(
                onNext = { itemIndexClicked ->

                    this.index = itemIndexClicked

                    isSoundChosen.accept(true)

                    // Will only update card view data if the timer is NOT running.
                    if (isTimerRunning.value != true) setCardViewData(itemIndexClicked)
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
        if (isTimerRunning) buttonText.value = getResourceString(context, R.string.pause)
        else buttonText.value = getResourceString(context, R.string.resume)


    private fun setCardViewData(index: Int) {
        val imageList = ItemData.getAllImageReferences()
        val textList = ItemData.getAllText(context)
        cardViewImage.postValue(imageList[index])
        cardViewText.postValue(textList[index])
    }

    private fun resetCardViewData() {
        cardViewImage.postValue(R.drawable.nosound)
        cardViewText.postValue(getResourceString(context, R.string.no_sound))
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