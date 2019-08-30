package com.takari.sleeplock.ui.feature

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.takari.sleeplock.R
import com.takari.sleeplock.data.Repository
import com.takari.sleeplock.ui.common.Animate
import com.takari.sleeplock.utils.getResourceString
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * Shared by TimerFragment and WhiteNoiseFragment. Scoped to the MainActivity.
 */
class SharedViewModel @Inject constructor(
    private val context: Context, // this is application context
    private val repository: Repository
) : ViewModel() {

    companion object {
        private const val TIME = "time"
        private const val NOISE = "white sound"
        private const val TIMER_ACTION = "timer action"
        private const val IMAGE = "image"
        private const val NAME = "name"
        private const val SOUND = "sound"
    }


    private val currentTime = MutableLiveData<Long>()

    private val timerCompleted = MutableLiveData<Unit>()

    private val buttonState = MutableLiveData<ButtonState>()

    private val timerAction = MutableLiveData<String>()

    private val whiteNoiseData by lazy {
        MutableLiveData(
            WhiteNoiseData(
                image = R.drawable.nosound,
                name = getString(R.string.no_sound),
                sound = null
            )
        )
    }

    private val animate = MutableLiveData<Long>()

    private val toast = MutableLiveData<ToastData>()

    private val isTimeChosen = BehaviorRelay.createDefault(false)

    private val isWhiteNoiseChosen = BehaviorRelay.createDefault(false)

    private val compositeDisposable = CompositeDisposable()

    private var chosenTime: Long? = null


    private val observeCurrentTime = repository.currentTime
        .subscribeOn(Schedulers.computation())
        .subscribeBy(
            onNext = { time -> currentTime.postValue(time) },
            onError = { Log.d("zwi", "Error observing currentTime in SharedViewModel: $it") }
        )
        .addTo(compositeDisposable)


    private val observeIsTimerRunning = repository.isTimerRunning
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onNext = { isRunning -> setTimerAction(isRunning, repository.hasTimerStartedBoolean()) },
            onError = { Log.d("zwi", "Error observing isTimerRunning in SharedViewModel: $it") }
        ).addTo(compositeDisposable)


    private val observeHasTimerCompleted = repository.timerCompleted
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onNext = {
                timerCompleted.postValue(Unit)
                resetState()
            },
            onError = { Log.d("zwi", "Error observing timerCompleted in SharedViewModel: $it") }
        ).addTo(compositeDisposable)


    private val observeHasTimerStarted = repository.hasTimerStarted
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onNext = { animate.postValue(Animate.DEFAULT) },
            onError = { Log.d("zwi", "Error observing hasTimerStarted in SharedViewModel: $it") }
        ).addTo(compositeDisposable)


    private val observeWhiteNoiseAndTime = Observables
        .combineLatest(isTimeChosen, isWhiteNoiseChosen)
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onNext = { (timeChosen, soundChosen) ->
                if (timeChosen && soundChosen)
                    buttonState.postValue(ButtonState(enabled = true, color = Color.LightBlue.hexCode))
                else
                    buttonState.postValue( ButtonState(enabled = false, color = Color.DarkBlue.hexCode))
            },
            onError = { Log.d("zwi", "Error observing white noise and time chosen in shared view model $it") }
        ).addTo(compositeDisposable)


    init {
        if (repository.isTimerRunningBoolean()) restoreState()
    }

    fun getCurrentTime(): LiveData<Long> = currentTime

    fun getTimerCompleted(): LiveData<Unit> = timerCompleted

    fun getButtonState(): LiveData<ButtonState> = buttonState

    fun getTimerAction(): LiveData<String> = timerAction

    fun getWhiteNoiseData(): LiveData<WhiteNoiseData> = whiteNoiseData

    fun getAnimate(): LiveData<Long> = animate

    fun getToast(): LiveData<ToastData> = toast


    fun startOrPauseTimer() {
        if (repository.isTimerRunningBoolean()) repository.pauseSoundAndTimer()
        else startOrResume()
    }

    private fun startOrResume() {
        if (repository.hasTimerStartedBoolean()) repository.resumeSoundAndTimer()

        // I took precautions to ensure the chosenTime and whiteNoise aren't null by this point.
        else repository.startSoundAndTimer(chosenTime!!, whiteNoiseData.value?.sound!!)
    }

    /**
    Resets the timer in TimerService and triggers an emission from timerCompleted in the Repository.
     */
    fun resetSoundAndTimer() = repository.resetSoundAndTimer()


    fun setTime(milliseconds: Long) {
        chosenTime = milliseconds
        isTimeChosen.accept(true) //todo which thread is this ran on?
    }

    fun setWhiteNoiseDataIfTimerRunning(data: WhiteNoiseData) {
        if (repository.isTimerRunningBoolean()) {
            whiteNoiseData.value = data
            isWhiteNoiseChosen.accept(true)
        }
    }

    fun getWhiteNoiseList() = repository.whiteNoiseList

    fun setToastData() {
        if (repository.hasTimerStartedBoolean()) toast.value = ToastData(
            type = ToastTypes.Warning.name,
            stringID = R.string.reset_the_timer
        )
        else toast.value = ToastData(
            type = ToastTypes.Success.name,
            stringID = R.string.sound_selected
        )
    }


    /**
     * Sets the value of timerAction depending on the timer state. If the timer is running it emits "Pause".
     * If the timer not running but has hasStarted (the user paused the timer) it emits "Resume". Else it emits "Start".
     */
    private fun setTimerAction(timerRunning: Boolean, timerStarted: Boolean) {
        when {
            timerRunning -> timerAction.value = getString(R.string.pause)
            timerStarted -> timerAction.value = getString(R.string.resume)
            else -> timerAction.value = getString(R.string.start)
        }
    }

    private fun saveState() = repository.apply {

        saveValueIfNonNull(TIME, isTimeChosen.value)

        saveValueIfNonNull(NOISE, isWhiteNoiseChosen.value)

        saveValueIfNonNull(TIMER_ACTION, timerAction.value)

        saveValueIfNonNull(IMAGE, whiteNoiseData.value?.image)

        saveValueIfNonNull(NAME, whiteNoiseData.value?.name)

        saveValueIfNonNull(SOUND, whiteNoiseData.value?.sound)
    }

    private fun restoreState() {

        isTimeChosen.accept(repository.getBoolean(TIME, false))
        isWhiteNoiseChosen.accept(repository.getBoolean(NOISE, false))

        timerAction.value = repository.getString(TIMER_ACTION, getString(R.string.start))

        whiteNoiseData.value = WhiteNoiseData(
            repository.getInt(IMAGE, R.drawable.nosound),
            repository.getString(NAME, getString(R.string.no_sound)),
            repository.getInt(SOUND, 0)
        )

        animate.value = Animate.INSTANT //instantly restores the animation state
    }

    private fun resetState() {
        isTimeChosen.accept(false)
        isWhiteNoiseChosen.accept(false)

        chosenTime = null
        resetWhiteNoiseData()

        setTimerAction(repository.isTimerRunningBoolean(), repository.hasTimerStartedBoolean())
    }

    private fun resetWhiteNoiseData() {
        whiteNoiseData.value = WhiteNoiseData(
            image = R.drawable.nosound,
            name = getString(R.string.no_sound),
            sound = null
        )
    }

    private fun getString(stringId: Int) = getResourceString(context, stringId)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()

        if (repository.isTimerRunningBoolean()) saveState()
    }
}

/**
Represents the data of the user selected white sound.
 */
data class WhiteNoiseData(
    val image: Int,
    val name: String,
    val sound: Int?
)

data class ToastData(
    val type: String,
    val stringID: Int
)

enum class ToastTypes {
    Success, Warning
}

data class ButtonState(
    val enabled: Boolean,
    val color: String
)

enum class Color(val hexCode: String) {
    DarkBlue("#0B3136"), LightBlue("#4dd0e1")
}