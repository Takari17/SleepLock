package com.takari.sleeplock.feature.sleeptimer.events

import android.content.Intent
import com.takari.sleeplock.feature.common.Animate

sealed class SleepTimerSingleEvent {
    data class StartAnimation(val duration: Animate.Duration) : SleepTimerSingleEvent()
    object ReverseAnimation : SleepTimerSingleEvent()
    object ShowQuestionView: SleepTimerSingleEvent()
    object HideQuestionView: SleepTimerSingleEvent()
    data class ShowWarningToast(val message: String) : SleepTimerSingleEvent()
    data class OpenAdminActivity(val intent: Intent) : SleepTimerSingleEvent()
    data class StartService(val millis: Long) : SleepTimerSingleEvent()
    object PauseService : SleepTimerSingleEvent()
    object ResumeService : SleepTimerSingleEvent()
    object ResetService : SleepTimerSingleEvent()
}