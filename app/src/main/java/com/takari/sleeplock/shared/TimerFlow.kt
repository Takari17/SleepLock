package com.takari.sleeplock.shared

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow


/**
 * A countdown timer that emit elapseTime and isRunning via stateflow StateFlow
 * until millis reaches 0.
 *
 * @param millis Staring time in milliseconds.
 */
class TimerFlow(private var millis: Long) {

    private var canceled = false

    // Separating elapseTime and isRunning helps to avoid dropping emissions.
    val elapseTime = MutableStateFlow(millis)
    val isRunning = MutableStateFlow(true)

    suspend fun start() {
        while (!canceled) {
            if (isRunning.value) {
                millis -= 1000
            }

            elapseTime.value = millis

            if (millis <= 0L) reset()

            delay(1000)
        }
    }

    fun resume() {
        isRunning.value = true
    }

    fun pause() {
        isRunning.value = false
    }

    fun reset() {
        isRunning.value = false
        canceled = true
    }
}