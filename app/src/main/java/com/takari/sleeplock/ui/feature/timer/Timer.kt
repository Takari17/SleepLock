package com.takari.sleeplock.ui.feature.timer

import com.jakewharton.rxrelay2.BehaviorRelay
import com.takari.sleeplock.utils.convertMilliToSeconds
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class Timer(millis: Long) {

    private val elapsedTime = AtomicLong()

    private var startingTime: Int = 0 // Used for stopping the timer at 0

    private val resumed = AtomicBoolean().also { it.set(false) }

    private val stopped = AtomicBoolean().also { it.set(false) }

    val isTimerRunning = BehaviorRelay.create<Boolean>()

    val hasTimerStarted = BehaviorRelay.create<Boolean>()

    val hasTimerCompleted = BehaviorRelay.create<Boolean>()

    /**
    Stop's if either reset is called or if the elapse time reaches 0.
     */
    val countDownTimer: Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)
        .takeWhile { !stopped.get() }
        .takeWhile { millis -> startingTime != millis.toInt() }
        .filter { resumed.get() }
        .map { elapsedTime.addAndGet(-1000) }
        .doOnComplete { reset() }


    init {
        elapsedTime.addAndGet(millis)
        startingTime = elapsedTime.toLong().convertMilliToSeconds()
    }

    fun start() {
        resumed.set(true)
        isTimerRunning.accept(true)
        hasTimerStarted.accept(true)
        hasTimerCompleted.accept(false)
    }

    fun resume() {
        resumed.set(true)
        isTimerRunning.accept(true)
    }

    fun pause() {
        resumed.set(false)
        isTimerRunning.accept(false)
    }

    fun reset() {
        stopped.set(true)
        hasTimerStarted.accept(false)
        isTimerRunning.accept(false)
        hasTimerCompleted.accept(true)
    }
}