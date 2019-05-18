package com.example.sleeplock.data.features

import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.utils.convertMilliToSeconds
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class Timer(millis: Long) : Operable {

    private val elapsedTime = AtomicLong()
    private val resumed = AtomicBoolean()
    private val stopped = AtomicBoolean()

    lateinit var currentTime: Flowable<Long>

    private var startingTime: Int = 0 // used for stopping the timer at 0

    val isTimerRunning = MutableLiveData(false)
    var isTimerPaused = MutableLiveData(false)

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

        currentTime = Flowable.interval(1, TimeUnit.SECONDS)
            .takeWhile { !stopped.get() }
            .takeWhile { aLong -> startingTime != (aLong).toInt() }
            .filter { resumed.get() }
            .map { elapsedTime.addAndGet(-1000) }
            .subscribeOn(Schedulers.io())
    }

    override fun start() {
        resumed.set(true)
        isTimerRunning.value = true
        isTimerPaused.value = false
    }

    override fun pause() {
        resumed.set(false)
        isTimerRunning.value = false
        isTimerPaused.value = true
    }

    override fun reset() {
        stopped.set(true)
        isTimerRunning.value = false
        isTimerPaused.value = false
    }
}