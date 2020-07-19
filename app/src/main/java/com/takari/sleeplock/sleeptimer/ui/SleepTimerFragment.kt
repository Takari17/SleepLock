package com.takari.sleeplock.sleeptimer.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.takari.sleeplock.App
import com.takari.sleeplock.R
import com.takari.sleeplock.shared.TimeSelectionDialog
import com.takari.sleeplock.sleeptimer.admin.PermissionResult
import com.takari.sleeplock.sleeptimer.service.SleepTimerService
import kotlinx.android.synthetic.main.sleep_timer_fragment.*


class SleepTimerFragment : Fragment() {

    companion object {
        const val DURATION_DEFAULT = 1000L
        const val DURATION_INSTANT = 0L
    }

    private val viewModel by viewModels<SleepTimerViewModel>()
    private var sleepTimerService: SleepTimerService? = null
    private val serviceIntent by lazy { Intent(requireContext(), SleepTimerService::class.java) }
    private val timeSelectionDialog = TimeSelectionDialog()
    private val adminPermissions = App.applicationComponent.adminPermission
    private val requestId = 2343345
    private val initialConstraints = ConstraintSet()
    private val altConstraint = ConstraintSet()
    private val transition = AutoTransition().apply {
        interpolator = AnticipateOvershootInterpolator(1f)
        duration = 1000
        ordering = AutoTransition.ORDERING_TOGETHER
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!adminPermissions.isEnabled()) requestDeviceAdminPermissions()
    }

    private fun requestDeviceAdminPermissions() {
        val requestIntent = adminPermissions.requestIntent
        startActivityForResult(requestIntent, requestId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestId) {
            if (resultCode == PermissionResult.UserCanceled.code)
                requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sleep_timer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialConstraints.clone(sleepTimerFragment)
        altConstraint.clone(requireContext(), R.layout.sleep_timer_fragment_alt)

        startPauseButton.setOnClickListener {
            val timerIsRunning = sleepTimerService?.timerIsRunning?.value ?: false
            viewModel.onStartPauseButtonClick(SleepTimerService.isRunning(), timerIsRunning)
        }

        cancelButton.setOnClickListener { cancelService() }

        timeSelectionDialog.onTimeSelected = { millis ->
            if (millis != 0L) {
                startService(millis)
                bindToService()
                startAnimation(DURATION_DEFAULT)
            }
        }

        viewModel.viewCommand = { viewCommand ->
            val placeHolder = when (viewCommand) {
                SleepTimerViewCommands.PauseService -> pauseService()
                SleepTimerViewCommands.ResumeService -> resumeService()
                SleepTimerViewCommands.DestroyService -> cancelService()
                SleepTimerViewCommands.OpenTimeSelectionDialog -> openTimeOptionsDialog()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (SleepTimerService.isRunning()) bindToService()
    }

    override fun onStop() {
        super.onStop()
        if (SleepTimerService.isRunning()) unBindFromService()
    }

    private fun openTimeOptionsDialog() {
        if (!timeSelectionDialog.isAdded)
            timeSelectionDialog.show(requireActivity().supportFragmentManager, "timeDialog")
    }

    private fun startAnimation(duration: Long) {
        transition.duration = duration
        TransitionManager.beginDelayedTransition(sleepTimerFragment, transition)
        altConstraint.applyTo(sleepTimerFragment)
    }

    private fun reverseAnimation() {
        transition.duration = DURATION_DEFAULT
        TransitionManager.beginDelayedTransition(sleepTimerFragment, transition)
        initialConstraints.applyTo(sleepTimerFragment)
    }

    private fun startService(millis: Long) {
        serviceIntent.apply {
            action = SleepTimerService.START
            putExtra(SleepTimerService.MILLIS, millis)
        }
        requireContext().startService(serviceIntent)
    }

    private fun pauseService() {
        sleepTimerService?.pauseTimer()
    }

    private fun resumeService() {
        sleepTimerService?.resumeTimer()
    }

    private fun cancelService() {
        sleepTimerService?.destroyService()
    }

    private fun bindToService() {
        requireContext().bindService(serviceIntent, serviceConnection, 0)
    }

    private fun unBindFromService() {
        requireContext().unbindService(serviceConnection)
    }

    private fun onBind() {
        startAnimation(DURATION_INSTANT)
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            sleepTimerService = (service as SleepTimerService.LocalBinder).getService()

            onBind()

            sleepTimerService?.elapseTime?.observe(viewLifecycleOwner, Observer { elapseTime ->
                currentTimeTextView.text = elapseTime
            })

            sleepTimerService?.timerIsRunning?.observe(viewLifecycleOwner, Observer { timerIsRunning ->
                    val newText = if (timerIsRunning) "Pause" else "Resume"
                    startPauseButton.setText(newText)
                })

            sleepTimerService?.onServiceCanceled = {
                lifecycleScope.launchWhenStarted {
                    reverseAnimation()
                    startPauseButton.setText("Start")
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}
