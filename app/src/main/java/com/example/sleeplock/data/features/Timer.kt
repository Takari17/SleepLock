package com.example.sleeplock.data.features

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.utils.convertMilliToSeconds
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/*
 * Self explanatory class (at least I hope so...).
 */
class Timer(millis: Long) {

    private val elapsedTime = AtomicLong() // In millis

    private var startingTime: Int = 0 // Used for stopping the timer at 0

    // Default values
    private val resumed = AtomicBoolean().also { it.set(false) }
    private val stopped = AtomicBoolean().also { it.set(false) }

    // Some useful callbacks we can observe
    val wasTimerStarted = BehaviorRelay.createDefault(false)
    val isTimerCompleted = PublishRelay.create<Boolean>()
    private val isTimerRunning = MutableLiveData<Boolean>()


    // Stop's if either reset() is called or if the elapse time reaches 0
    val currentTime: Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)
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
        wasTimerStarted.accept(true)
        isTimerRunning.postValue(true)
    }

    fun resume() {
        resumed.set(true)
        isTimerRunning.postValue(true)
    }

    fun pause() {
        resumed.set(false)
        isTimerRunning.postValue(false)
    }

    fun reset() {
        stopped.set(true)
        wasTimerStarted.accept(false)
        isTimerRunning.postValue(null)
        isTimerCompleted.accept(true)
    }

    fun getIsTimerRunning(): LiveData<Boolean> = isTimerRunning
}