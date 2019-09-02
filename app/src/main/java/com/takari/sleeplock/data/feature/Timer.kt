package com.takari.sleeplock.data.feature

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

    private val isRunning = BehaviorRelay.create<Boolean>()

    private val hasStarted = BehaviorRelay.create<Boolean>()

    private val completed = BehaviorRelay.create<Unit>()

    /**
    Hot observable that emit's emits a count down timer, can be started, paused,
    resumed and reseted. Stop's if either reset is called or if the elapse time reaches 0.
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
        isRunning.accept(true)
        hasStarted.accept(true)
    }

    fun resume() {
        resumed.set(true)
        isRunning.accept(true)
    }

    fun pause() {
        resumed.set(false)
        isRunning.accept(false)
    }

    fun reset() {
        stopped.set(true)
        hasStarted.accept(false)
        isRunning.accept(false)
        completed.accept(Unit)
    }

    fun getIsRunning(): Observable<Boolean> = isRunning

    fun getHasStarted(): Observable<Boolean> = hasStarted

    fun getCompleted(): Observable<Unit> = completed
}