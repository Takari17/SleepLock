package com.takari.sleeplock.feature.sleeptimer.ui

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.takari.sleeplock.feature.sleeptimer.SleepTimerRepository
import com.takari.sleeplock.feature.sleeptimer.admin.AdminPermissions
import com.takari.sleeplock.feature.sleeptimer.events.SleepTimerSingleEvent
import com.takari.sleeplock.feature.sleeptimer.events.SleepTimerViewEvent
import com.takari.sleeplock.feature.common.Animate
import com.takari.sleeplock.feature.common.ButtonStateColor
import com.takari.sleeplock.feature.common.Timer
import com.takari.sleeplock.feature.common.logD
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


class SleepTimerViewModel @Inject constructor(
    private val repository: SleepTimerRepository,
    private val defaultState: SleepTimerViewState,
    private val adminPermissions: AdminPermissions
) : ViewModel() {

    private val state = BehaviorRelay.createDefault(defaultState)
    private val singleEvent = PublishRelay.create<SleepTimerSingleEvent>()
    private val compositeDisposable = CompositeDisposable()


    init {
        compositeDisposable += repository.currentTime.subscribeBy { millis ->
            state.updateCurrentTime(millis)
        }

        compositeDisposable += repository.timerAction.subscribeBy { timerAction ->
            state.updateTimerAction(timerAction)
        }

        compositeDisposable += repository.completed.subscribeBy { resetState() }
    }

    fun viewEvent(viewEvent: SleepTimerViewEvent) {
        when (viewEvent) {

            is SleepTimerViewEvent.OnCreateFinish ->
                if (!repository.hideQuestionView())
                    singleEvent.accept(SleepTimerSingleEvent.ShowQuestionView)

            is SleepTimerViewEvent.OnStart ->
                if (repository.hasTimerStarted) restoreState()

            is SleepTimerViewEvent.OnQuestionDialogSuccessClick -> {
                singleEvent.accept(SleepTimerSingleEvent.HideQuestionView)
                repository.saveHideQuestionView(true)
            }

            is SleepTimerViewEvent.OnStartPauseClick -> startPauseButtonClicked()

            is SleepTimerViewEvent.OnResetClick -> singleEvent.accept(SleepTimerSingleEvent.ResetService)

            is SleepTimerViewEvent.OnUserSelectsTime -> {
                state.apply {
                    updateCurrentTime(viewEvent.millis)
                    updateIsEnabled(true)
                    updateColor(ButtonStateColor.Enabled)
                }
            }
        }
    }


    private fun startPauseButtonClicked() {
        when {
            !adminPermissions.status() -> requestAdminPermission()

            repository.isTimerRunning -> singleEvent.accept(SleepTimerSingleEvent.PauseService)

            repository.hasTimerStarted -> singleEvent.accept(SleepTimerSingleEvent.ResumeService)

            repository.whiteNoiseServiceRunning() -> singleEvent.accept(SleepTimerSingleEvent.ShowWarningToast("Reset WhiteNoise"))

            else -> {
                singleEvent.accept(
                    SleepTimerSingleEvent.StartService(state.value!!.currentTime)
                )
                singleEvent.accept(
                    SleepTimerSingleEvent.StartAnimation(Animate.Duration.DEFAULT)
                )
            }
        }
    }


    private fun requestAdminPermission() {
        singleEvent.accept(SleepTimerSingleEvent.OpenAdminActivity(adminPermissions.requestIntent))
    }


    //call in "complete" subscription/ from the repo
    private fun resetState() {
        singleEvent.accept(SleepTimerSingleEvent.ReverseAnimation)
        state.accept(defaultState)
    }

    private fun restoreState() {
        singleEvent.accept(SleepTimerSingleEvent.StartAnimation(Animate.Duration.INSTANT))
        state.updateIsEnabled(true)
        state.updateColor(ButtonStateColor.Enabled)
    }


    fun singleEvent(): Observable<SleepTimerSingleEvent> = singleEvent

    fun state(): Observable<SleepTimerViewState> = state

    private fun BehaviorRelay<SleepTimerViewState>.updateCurrentTime(millis: Long) {
        accept(this.value!!.copy(currentTime = millis))
    }

    private fun BehaviorRelay<SleepTimerViewState>.updateColor(color: ButtonStateColor) {
        accept(this.value!!.copy(color = color))
    }

    private fun BehaviorRelay<SleepTimerViewState>.updateIsEnabled(enabled: Boolean) {
        accept(this.value!!.copy(isEnabled = enabled))
    }

    private fun BehaviorRelay<SleepTimerViewState>.updateTimerAction(action: Timer.Action) {
        accept(this.value!!.copy(timerAction = action))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
