package com.takari.sleeplock.shared

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow


//todo unit test
class TimerFlow(millis: Long, private val isRunning: (Boolean) -> Unit = {}) {

    private var elapsedTime = millis
    private var running = true
    private var canceled = false

    val get = flow<Long> {
        while (!canceled) {

            if (running) {
                //timer is not canceled or paused
                elapsedTime -= 1000
                emit(elapsedTime)
            }

            if (elapsedTime <= 0L) reset()

            delay(1000)
        }
    }

    //collecting the flow starts it, so we don't need a start() function
    fun resume() {
        running = true
        isRunning(true) //todo not a fan of this duplication
    }

    fun pause() {
        running = false
        isRunning(false)
    }

    fun reset() {
        canceled = true
        running = false
        isRunning(false)
    }

    fun isTimerRunning() = running
}