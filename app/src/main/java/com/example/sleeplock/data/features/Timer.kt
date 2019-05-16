package com.example.sleeplock.data.features

import com.example.sleeplock.utils.convertMilliToSeconds
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

// Only modified within this class
var isTimerRunning = false
var isTimerPaused = false

class Timer(millis: Long) {

    private val elapsedTime = AtomicLong()
    private val resumed = AtomicBoolean()
    private val stopped = AtomicBoolean()

    lateinit var currentTime: Flowable<Long>

    private var startingTime: Int = 0 // used for stopping the timer at 0z

    init {
        val seconds = millis.convertMilliToSeconds()
        prepareTimer(seconds)
        setTimer()
    }

    private fun prepareTimer(seconds: Int) {
        elapsedTime.addAndGet((seconds * 1000).toLong())
        startingTime = (elapsedTime.toLong()).convertMilliToSeconds()
    }

    private fun setTimer() {
        resumed.set(false)
        stopped.set(false)

        // runs on a background thread
        currentTime = Flowable.interval(1, TimeUnit.SECONDS)
            .takeWhile { !stopped.get() }
            .takeWhile { aLong -> startingTime != (aLong).toInt() }
            .filter { resumed.get() }
            .map { elapsedTime.addAndGet(-1000) }
    }

    fun start() {
        resumed.set(true)
        isTimerRunning = true
        isTimerPaused = false
    }

    fun pause() {
        resumed.set(false)
        isTimerRunning = false
        isTimerPaused = true
    }

    fun reset() {
        stopped.set(true)
        isTimerRunning = false
        isTimerPaused = false
    }
}
