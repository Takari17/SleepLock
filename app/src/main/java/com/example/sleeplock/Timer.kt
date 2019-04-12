package com.example.sleeplock

import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong


class Timer(millis: Long) {

    private val elapsedTime = AtomicLong()
    private val resumed = AtomicBoolean()
    private val stopped = AtomicBoolean()

    lateinit var currentTime: Flowable<Long>

    private var startingTime: Int = 0 // used for stopping the timer at 0

    init {
        val seconds = millis.convertMilliToSeconds()
        setTimer(seconds)
        setFlowable()
    }

    private fun setTimer(seconds: Int) {
        elapsedTime.addAndGet((seconds * 1000).toLong())
        startingTime = (elapsedTime.toLong()).convertMilliToSeconds()
    }

    private fun setFlowable() {
        resumed.set(false)
        stopped.set(false)

        // runs on a background thread
        currentTime = Flowable.interval(1, TimeUnit.SECONDS)
            .takeWhile { !stopped.get() }
            .takeWhile { aLong -> startingTime != (aLong).toInt() }
            .filter { resumed.get() }
            .map { elapsedTime.addAndGet(-1000) }
    }

    fun startTimer() = resumed.set(true)

    fun pauseTimer() = resumed.set(false)

    fun resetTimer() = stopped.set(true)

}
