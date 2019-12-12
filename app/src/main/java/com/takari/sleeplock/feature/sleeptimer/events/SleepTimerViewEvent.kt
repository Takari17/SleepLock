package com.takari.sleeplock.feature.sleeptimer.events

sealed class SleepTimerViewEvent{
    object OnCreateFinish: SleepTimerViewEvent()
    object OnStart: SleepTimerViewEvent()
    object OnQuestionDialogSuccessClick: SleepTimerViewEvent()
    object OnStartPauseClick : SleepTimerViewEvent()
    object OnResetClick : SleepTimerViewEvent()
    data class OnUserSelectsTime(val millis: Long) : SleepTimerViewEvent()
}