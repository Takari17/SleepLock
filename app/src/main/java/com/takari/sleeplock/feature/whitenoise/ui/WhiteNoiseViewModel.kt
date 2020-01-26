package com.takari.sleeplock.feature.whitenoise.ui

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.takari.sleeplock.feature.common.Animate
import com.takari.sleeplock.feature.common.ButtonStateColor
import com.takari.sleeplock.feature.common.Timer
import com.takari.sleeplock.feature.sleeptimer.service.SleepTimerService
import com.takari.sleeplock.feature.whitenoise.data.WhiteNoiseRepository
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import com.takari.sleeplock.feature.whitenoise.events.WhiteNoiseSingleEvent
import com.takari.sleeplock.feature.whitenoise.events.WhiteNoiseViewEvent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


class WhiteNoiseViewModel @Inject constructor(
    private val defaultState: WhiteNoiseViewState,
    private val repository: WhiteNoiseRepository
) : ViewModel() {

    private val state = BehaviorRelay.createDefault(defaultState)
    private val singleEvent = PublishRelay.create<WhiteNoiseSingleEvent>()
    private val timeChosen = BehaviorRelay.create<Boolean>()
    private val whiteNoiseChosen = BehaviorRelay.create<Boolean>()
    private val compositeDisposable = CompositeDisposable()


    init {
        compositeDisposable += Observables.combineLatest(timeChosen, whiteNoiseChosen)
            .subscribeBy { (timeChosen, whiteNoiseChosen) ->
                if (timeChosen && whiteNoiseChosen) {
                    state.updateIsEnabled(true)
                    state.updateColor(ButtonStateColor.Enabled)
                } else {
                    state.updateIsEnabled(false)
                    state.updateColor(ButtonStateColor.Disabled)
                }
            }


        compositeDisposable += repository.currentTime.subscribeBy { millis ->
            state.updateCurrentTime(millis)
        }

        compositeDisposable += repository.timerAction.subscribeBy { timerAction ->
            state.updateTimerAction(timerAction)
        }

        compositeDisposable += repository.completed.subscribeBy { resetState() }
    }

    fun viewEvent(viewEvent: WhiteNoiseViewEvent) {
        when (viewEvent) {

            is WhiteNoiseViewEvent.OnStart -> {
                if (repository.hasTimerStarted) restoreState()
            }

            is WhiteNoiseViewEvent.OnStop ->
                if (repository.hasTimerStarted) repository.saveState(state.value!!)

            is WhiteNoiseViewEvent.OnStartPauseClick -> startPauseButtonClicked()

            is WhiteNoiseViewEvent.OnOpenWhiteNoiseOptions -> {
                if (!repository.hasTimerStarted)
                    singleEvent.accept(WhiteNoiseSingleEvent.OpenSoundDialog)
                else
                    singleEvent.accept(WhiteNoiseSingleEvent.ShowWarningToast("Reset the Timer"))
            }

            is WhiteNoiseViewEvent.OnUserSelectsTime -> {
                state.updateCurrentTime(viewEvent.millis)
                timeChosen.accept(true)
            }

            is WhiteNoiseViewEvent.OnUserSelectsWhiteNoise -> {
                state.updateWhiteNoise(viewEvent.whiteNoise)
                whiteNoiseChosen.accept(true)
            }
        }
    }

    private fun startPauseButtonClicked() {
        when {
            repository.isTimerRunning -> singleEvent.accept(WhiteNoiseSingleEvent.PauseService)

            repository.hasTimerStarted -> singleEvent.accept(WhiteNoiseSingleEvent.ResumeService)

            !SleepTimerService.isRunning() -> {
                // we ensured that these values wouldn't be null by disabling the button until the user selected a time and white noise :p
                singleEvent.accept(
                    WhiteNoiseSingleEvent.StartService(
                        millis = state.value!!.currentTime,
                        whiteNoise = state.value!!.whiteNoise!!
                    )
                )
                singleEvent.accept(WhiteNoiseSingleEvent.StartAnimation(Animate.Duration.DEFAULT))
            }
            else -> singleEvent.accept(WhiteNoiseSingleEvent.ShowWarningToast("Reset SleepTimer"))
        }
    }

    private fun resetState() {
        singleEvent.accept(WhiteNoiseSingleEvent.ReverseAnimation)
        timeChosen.accept(false)
        whiteNoiseChosen.accept(false)
        state.accept(defaultState)
    }

    private fun restoreState() {
        state.accept(repository.restoreState())
        state.updateTimerAction(repository.timerAction.value ?: Timer.Action.Start)
        singleEvent.accept(WhiteNoiseSingleEvent.StartAnimation(Animate.Duration.INSTANT))
    }

    fun singleEvent(): Observable<WhiteNoiseSingleEvent> = singleEvent

    fun state() = state

    /*
    Used to update individual properties instead of overriding the whole thing with each new update. A
    default value is provided, so it's value will never be null
     */
    private fun BehaviorRelay<WhiteNoiseViewState>.updateCurrentTime(millis: Long) {
        accept(this.value!!.copy(currentTime = millis))
    }

    private fun BehaviorRelay<WhiteNoiseViewState>.updateWhiteNoise(whiteNoise: WhiteNoise) {
        accept(this.value!!.copy(whiteNoise = whiteNoise))
    }

    private fun BehaviorRelay<WhiteNoiseViewState>.updateColor(color: ButtonStateColor) {
        accept(this.value!!.copy(color = color))
    }

    private fun BehaviorRelay<WhiteNoiseViewState>.updateIsEnabled(enabled: Boolean) {
        accept(this.value!!.copy(isEnabled = enabled))
    }

    private fun BehaviorRelay<WhiteNoiseViewState>.updateTimerAction(action: Timer.Action) {
        accept(this.value!!.copy(timerAction = action))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        if (repository.hasTimerStarted) repository.saveState(state.value!!)
    }
}
