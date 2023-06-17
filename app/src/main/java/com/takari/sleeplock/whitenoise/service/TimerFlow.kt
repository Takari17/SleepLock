package com.takari.sleeplock.whitenoise.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow


//TODO write docs
class TimerFlow(private var millis: Long) {

    data class TimerState(val elapseTime: Long, val isTimerRunning: Boolean)

    private var running = true
    private var canceled = false

    val get = MutableStateFlow(TimerState(elapseTime = millis, isTimerRunning = false))

    suspend fun start() {
        while (!canceled) {
            if (running) {
                millis -= 1000
            }

            get.emit(TimerState(elapseTime = millis, isTimerRunning = running))

            if (millis <= 0L) reset()

            delay(1000)
        }
    }


    //collecting the flow starts it, so we don't need a start() function
    fun resume() {
        running = true
    }

    fun pause() {
        running = false
    }

    fun reset() {
        canceled = true
        running = false
    }
}