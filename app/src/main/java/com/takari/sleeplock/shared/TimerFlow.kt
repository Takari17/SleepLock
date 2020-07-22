package com.takari.sleeplock.shared

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class TimerFlow(private var millis: Long, private val timerIsRunning: (Boolean) -> Unit = {}) {

    private var running = true
    private var canceled = false

    val get = flow<Long> {
        while (!canceled) {

            if (running) {
                //timer is not canceled or paused
                millis -= 1000
                emit(millis)
            }

            if (millis <= 0L) reset()

            delay(1000)
        }
    }

    //collecting the flow starts it, so we don't need a start() function
    fun resume() {
        running = true
        timerIsRunning(true)
    }

    fun pause() {
        running = false
        timerIsRunning(false)
    }

    fun reset() {
        canceled = true
        running = false
        timerIsRunning(false)
    }
}