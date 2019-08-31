package com.takari.sleeplock.ui.feature.timer

import android.content.Context
import com.jakewharton.rxrelay2.BehaviorRelay
import com.takari.sleeplock.R
import com.takari.sleeplock.data.Repository
import com.takari.sleeplock.ui.common.Animate
import com.takari.sleeplock.ui.feature.*
import com.takari.sleeplock.utils.getResourceString
import com.utils.InstantExecutorExtension
import com.utils.blockingObserve
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class SharedViewModelTest {

    private val context = mockk<Context>()
    private val repository = mockk<Repository>()
    private lateinit var sharedViewModel: SharedViewModel


    @BeforeAll
    fun beforeAll() {
        // Replaces IO scheduler with trampoline scheduler
        Schedulers.trampoline().apply { RxJavaPlugins.setInitIoSchedulerHandler { this } }
    }


    @BeforeEach
    fun beforeEach() {
        /*
        Fully resets the testing environment, was banging my head against the wall over a bug bc I wasn't
        resetting the environment for our test. The state of the view model would carry from one test to another
        which would yield inconsistent results.
        */

        every { getResourceString(context, R.string.no_sound) } returns "No Sound"
        every { getResourceString(context, R.string.start) } returns "Start"
        every { getResourceString(context, R.string.pause) } returns "Pause"
        every { getResourceString(context, R.string.resume) } returns "Resume"

        every { repository.currentTime } returns BehaviorRelay.create()
        every { repository.isTimerRunning } returns BehaviorRelay.create()
        every { repository.timerCompleted } returns BehaviorRelay.create()
        every { repository.hasTimerStarted } returns BehaviorRelay.create()
        every { repository.isTimerRunningBoolean() } returns false

        sharedViewModel = SharedViewModel(context, repository)
    }

    @Test
    fun `current time updates correctly`() {

        repository.currentTime.accept(100)

        assertEquals(100, sharedViewModel.getCurrentTime().blockingObserve())
    }


    @Test
    fun `setTimerAction should emit Pause`() {

        every { repository.hasTimerStartedBoolean() } returns false

        repository.isTimerRunning.accept(true)

        assertEquals("Pause", sharedViewModel.getTimerAction().blockingObserve())
    }

    @Test
    fun `setTimerAction should emit Resume`() {

        every { repository.hasTimerStartedBoolean() } returns true

        repository.isTimerRunning.accept(false)

        assertEquals("Resume", sharedViewModel.getTimerAction().blockingObserve())
    }


    @Test
    fun `setTimerAction should emit Start`() {

        every { repository.hasTimerStartedBoolean() } returns false

        repository.isTimerRunning.accept(false)

        assertEquals("Start", sharedViewModel.getTimerAction().blockingObserve())
    }


    @Test
    fun `the state should reset`() {

        repository.timerCompleted.accept(Unit)

        assertEquals(
            ButtonState(false, "#0B3136"),
            sharedViewModel.getButtonState().blockingObserve()
        )

        assertEquals(
            WhiteNoiseData(R.drawable.nosound, "No Sound", null),
            sharedViewModel.getWhiteNoiseData().blockingObserve()
        )

        assertEquals("Start", sharedViewModel.getTimerAction().blockingObserve())
    }

    @Test
    fun `animate duration should be default`() {

        repository.hasTimerStarted.accept(true)

        assertEquals(Animate.DEFAULT, sharedViewModel.getAnimate().blockingObserve())
    }

    @Test
    fun `buttonState should be false and dark blue if time and sound are not chosen`() {

        assertEquals(
            ButtonState(enabled = false, color = Color.DarkBlue.hexCode),
            sharedViewModel.getButtonState().blockingObserve()
        )
    }

    @Test
    fun `buttonState should be false and dark blue if only sound is chosen`() {

        sharedViewModel.setWhiteNoiseData_IfTimerNotRunning(WhiteNoiseData(0, "", null))

        assertEquals(
            ButtonState(enabled = false, color = Color.DarkBlue.hexCode),
            sharedViewModel.getButtonState().blockingObserve()
        )
    }

    @Test
    fun `buttonState should be false and dark blue if only time is chosen`() {

        sharedViewModel.setTime(0)

        assertEquals(
            ButtonState(enabled = false, color = Color.DarkBlue.hexCode),
            sharedViewModel.getButtonState().blockingObserve()
        )
    }

    @Test
    fun `buttonState should be true and light blue if both time and sound are chosen`() {

        sharedViewModel.setTime(0)

        sharedViewModel.setWhiteNoiseData_IfTimerNotRunning(WhiteNoiseData(0, "", null))

        assertEquals(
            ButtonState(enabled = true, color = Color.LightBlue.hexCode),
            sharedViewModel.getButtonState().blockingObserve()
        )
    }


    @Test
    fun `startSoundAndTimer should be called`() {

        every { repository.startSoundAndTimer(0, 0) } returns Unit

        every { repository.isTimerRunningBoolean() } returns false

        every { repository.hasTimerStartedBoolean() } returns false

        sharedViewModel.setTime(0)
        sharedViewModel.setWhiteNoiseData_IfTimerNotRunning(WhiteNoiseData(0, "", 0))

        sharedViewModel.startOrPauseTimer()

        verify { repository.startSoundAndTimer(0, 0) }
    }

    @Test
    fun `pauseSoundAndTimer should be called`() {

        every { repository.pauseSoundAndTimer() } returns Unit

        every { repository.isTimerRunningBoolean() } returns true

        sharedViewModel.startOrPauseTimer()

        verify { repository.pauseSoundAndTimer() }
    }

    @Test
    fun `resumeSoundAndTimer should be called`() {

        every { repository.resumeSoundAndTimer() } returns Unit

        every { repository.isTimerRunningBoolean() } returns false

        every { repository.hasTimerStartedBoolean() } returns true

        sharedViewModel.startOrPauseTimer()

        verify { repository.resumeSoundAndTimer() }
    }

    @Test
    fun `whiteNoiseData should not update if timer is running`() {

        every { repository.isTimerRunningBoolean() } returns true

        sharedViewModel.setWhiteNoiseData_IfTimerNotRunning(WhiteNoiseData(5, "Test", 5))

        assertEquals(
            WhiteNoiseData(
                image = R.drawable.nosound,
                name = getResourceString(context, R.string.no_sound),
                sound = null
            ), sharedViewModel.getWhiteNoiseData().value
        )
    }

    @Test
    fun `warning ToastData should emit if timer has started`() {

        every { repository.hasTimerStartedBoolean() } returns true

        sharedViewModel.setToastData()

        assertEquals(
            ToastData(
                type = ToastTypes.Warning.name,
                stringID = R.string.reset_the_timer
            ), sharedViewModel.getToast().value
        )
    }

    @Test
    fun `success ToastData should emit if timer has not started`() {

        every { repository.hasTimerStartedBoolean() } returns false

        sharedViewModel.setToastData()

        assertEquals(
            ToastData(
                type = ToastTypes.Success.name,
                stringID = R.string.sound_selected
            ), sharedViewModel.getToast().value
        )
    }
}
