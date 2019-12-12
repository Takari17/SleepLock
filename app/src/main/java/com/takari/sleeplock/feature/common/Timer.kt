package com.takari.sleeplock.feature.common

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong


class Timer(
    private val millis: Long,
    private val isRunning: (Boolean) -> Unit,
    private val hasStarted: (Boolean) -> Unit
) {

    // atomic's are used for thread safety
    private val elapsedTime = AtomicLong().apply { addAndGet(millis) }
    private val resumed = AtomicBoolean().apply { set(false) }
    private val stopTimer = AtomicBoolean().apply { set(false) }

    val currentTime: Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)
        .takeWhile { !stopTimer.get() }
        .takeWhile { elapsedTime.get() != 0L }
        .filter { resumed.get() }
        .map { elapsedTime.addAndGet(-1000) }


    fun start() {
        resumed.set(true)
        isRunning(true)
        hasStarted(true)
    }

    fun pause() {
        resumed.set(false)
        isRunning(false)
    }

    fun resume() {
        resumed.set(true)
        isRunning(true)
    }

    fun reset() {
        hasStarted(false)
        isRunning(false)
        stopTimer.set(true)
    }

    enum class Action{
        Start, Pause, Resume
    }
}
