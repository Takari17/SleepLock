package com.example.sleeplock

import android.app.Application
import io.mockk.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class ViewModelTest {

    lateinit var viewModel: ViewModel

    @Before
    fun setUp() {
        val application = mockk<Application>()
        viewModel = ViewModel(application)
    }

    @Test
    fun getUpdateCurrentTime() {
    }

    @Test
    fun getItemIndexLD() {
    }

    @Test
    fun getEnabledDisabled() {
    }

    @Test
    fun getUpdateButtonColor() {
    }

    @Test
    fun getTimer() {
    }

    @Test
    fun setTimer() {
    }

    @Test
    fun isTimerRunning() {
    }

    @Test
    fun setTimerRunning() {
    }

    @Test
    fun getStartButton() {
    }

    @Test
    fun setStartButton() {
    }

    @Test
    fun subscribeToDialog() {
    }

    @Test
    fun startTimer() {
    }

    @Test
    fun pauseTimer() {
    }

    @Test
    fun resetTimer() {
    }

    @Test
    fun maybeStartService() {
    }

    @Test
    fun sendColor() {

    }
}