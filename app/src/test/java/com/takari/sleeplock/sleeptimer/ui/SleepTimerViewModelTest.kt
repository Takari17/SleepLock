package com.takari.sleeplock.sleeptimer.ui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SleepTimerViewModelTest {

    private lateinit var viewModel: SleepTimerViewModel

    @BeforeEach
    fun setUp() {
        viewModel = SleepTimerViewModel()
    }

    @Test
    fun `viewCommand should emit PauseService`() {
        viewModel.viewCommand = { command ->
            assertEquals(SleepTimerViewCommands.PauseService, command)
        }

        viewModel.onStartPauseButtonClick(serviceIsRunning = true, timerIsRunning = true)
    }

    @Test
    fun `viewCommand should emit ResumeService`() {
        viewModel.viewCommand = { command ->
            assertEquals(SleepTimerViewCommands.ResumeService, command)
        }

        viewModel.onStartPauseButtonClick(serviceIsRunning = true, timerIsRunning = false)
    }

    @Test
    fun `viewCommand should emit OpenTimeSelectionDialog`() {
        viewModel.viewCommand = { command ->
            assertEquals(SleepTimerViewCommands.OpenTimeSelectionDialog, command)
        }

        viewModel.onStartPauseButtonClick(serviceIsRunning = false, timerIsRunning = true)
    }
}