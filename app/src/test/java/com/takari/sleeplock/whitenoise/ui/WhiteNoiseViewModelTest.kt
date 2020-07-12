package com.takari.sleeplock.whitenoise.ui

import com.takari.sleeplock.InstantExecutorExtension
import com.takari.sleeplock.whitenoise.data.sounds.Rain
import com.takari.sleeplock.whitenoise.msc.TimerAction
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
internal class WhiteNoiseViewModelTest {

    private lateinit var viewModel: WhiteNoiseViewModel


    @BeforeEach
    fun beforeEach() {
        viewModel = WhiteNoiseViewModel()
    }


    @Test
    fun `should return PauseService command`() {

        viewModel.viewCommand = { command ->
            assertEquals(WhiteNoiseViewCommands.PauseService, command)
        }

        viewModel.onAdapterClick(
            clickedWhiteNoise = mockk(),
            serviceIsRunning = true,
            timerIsRunning = true
        )
    }

    @Test
    fun `should return ResumeService command`() {

        viewModel.viewCommand = { command ->
            assertEquals(WhiteNoiseViewCommands.ResumeService, command)
        }

        viewModel.onAdapterClick(
            clickedWhiteNoise = mockk(),
            serviceIsRunning = true,
            timerIsRunning = false
        )
    }

    @Test
    fun `should return OpenTimeSelectionDialog command`() {

        viewModel.viewCommand = { command ->
            assertEquals(WhiteNoiseViewCommands.OpenTimeSelectionDialog, command)
        }

        viewModel.onAdapterClick(
            clickedWhiteNoise = mockk(),
            serviceIsRunning = false,
            timerIsRunning = false
        )
    }

    @Test
    fun `viewCommand should return nothing if millis is 0`() {

        viewModel.viewCommand = { throw Exception("Should not of ran if millis was 0") }

        viewModel.onUserSelectedTimeFromDialog(millis = 0)
    }

    @Test
    fun `viewCommand should return StartAnimation command`() {

        //only asserts the first value passed
        var ticker = 0

        viewModel.viewCommand = { command ->
            if (ticker == 0)
                assertEquals(WhiteNoiseViewCommands.StartAnimation, command)

            ticker++
        }

        viewModel.onUserSelectedTimeFromDialog(millis = 1000)
    }

    @Test
    fun `viewCommand should return StartAndBindToService command`() {

        //only asserts the second value passed
        var ticker = 0

        viewModel.viewCommand = { command ->
            if (ticker == 1)
                assertEquals(WhiteNoiseViewCommands.StartAndBindToService(1000, Rain()), command)

            ticker++
        }

        viewModel.onUserSelectedTimeFromDialog(millis = 1000)
    }

    @Test
    fun `viewCommand should return DestroyService command`() {
        viewModel.viewCommand = { command ->
            assertEquals(WhiteNoiseViewCommands.DestroyService, command)
        }

        viewModel.onResetButtonClick()
    }

    @Test
    fun `timerActionIcon should return Play enum`() {
        viewModel.setTimerActionIcon(isTimerRunning = false)

        assertEquals(TimerAction.Play, viewModel.timerActionIcon.value)
    }

    @Test
    fun `timerActionIcon should return Pause enum`() {
        viewModel.setTimerActionIcon(isTimerRunning = true)

        assertEquals(TimerAction.Pause, viewModel.timerActionIcon.value)
    }

    @Test
    fun `viewModel state should reset`() {
        //sets some state
        viewModel.setTimerActionIcon(isTimerRunning = true)
        viewModel.isViewBindedToService = true

        //resets state
        viewModel.resetState()

        assertEquals(TimerAction.Play, viewModel.timerActionIcon.value)
        assertEquals(false, viewModel.isViewBindedToService)
    }
}