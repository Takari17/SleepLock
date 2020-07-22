package com.takari.sleeplock.whitenoise.ui

import android.content.ComponentName
import android.content.Context
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.takari.sleeplock.R
import com.takari.sleeplock.shared.TimeSelectionDialog
import com.takari.sleeplock.shared.to24HourFormat
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.data.WhiteNoiseOptions
import com.takari.sleeplock.whitenoise.data.sounds.Rain
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService.Companion.INIT_AND_START
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService.Companion.MILLIS
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService.Companion.WHITE_NOISE
import kotlinx.android.synthetic.main.white_noise_fragment.*


class WhiteNoiseFragment : Fragment() {

    private val viewModel by activityViewModels<WhiteNoiseViewModel>()
    private val timeSelectionDialog = TimeSelectionDialog()
    private var whiteNoiseService: WhiteNoiseService? = null
    private val serviceIntent by lazy { Intent(context, WhiteNoiseService::class.java) }
    private lateinit var zoomingLayoutManager: ZoomingLayoutManager
    private val initialConstraints = ConstraintSet()
    private val altConstraint = ConstraintSet()
    private val transition = AutoTransition().apply {
        interpolator = AnticipateOvershootInterpolator(2f)
        duration = 1000
        ordering = AutoTransition.ORDERING_TOGETHER
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.white_noise_fragment, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialConstraints.clone(whiteNoiseFragment)
        altConstraint.clone(requireContext(), R.layout.white_noise_fragment_alt)

        val whiteNoiseAdapter =
            WhiteNoiseAdapter(WhiteNoiseOptions.get, requireContext()) { clickedWhiteNoise ->

                val isTimerRunning = whiteNoiseService?.isTimerRunning?.value ?: false

                viewModel.onAdapterClick(
                    clickedWhiteNoise,
                    WhiteNoiseService.isRunning(),
                    isTimerRunning
                )
            }

        zoomingLayoutManager = ZoomingLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        whiteNoiseRecyclerView.apply {
            setHasFixedSize(true)
            adapter = whiteNoiseAdapter
            layoutManager = zoomingLayoutManager
            LinearSnapHelper().attachToRecyclerView(this)
            pagerIndicator.attachToRecyclerView(this)
        }

        //invokes onServiceDestroy in serviceConnection below
        resetButton.setOnClickListener { viewModel.onResetButtonClick() }

        timeSelectionDialog.onTimeSelected = { millis ->
            viewModel.onUserSelectedTimeFromDialog(millis)
        }

        viewModel.timerActionIcon.observe(viewLifecycleOwner, Observer { timerAction ->
            Glide.with(requireContext())
                .load(timerAction.iconResource)
                .into(timerActionIcon)
        })

        viewModel.viewCommand = { command ->
            val placeHolder = when (command) {
                is WhiteNoiseViewCommands.StartAndBindToService -> startService(
                    command.millis,
                    command.whiteNoise
                ).also { bindToService() }
                is WhiteNoiseViewCommands.PauseService -> pauseService()
                is WhiteNoiseViewCommands.ResumeService -> resumeService()
                is WhiteNoiseViewCommands.DestroyService -> destroyService()
                is WhiteNoiseViewCommands.OpenTimeSelectionDialog -> openTimeOptionsDialog()
                is WhiteNoiseViewCommands.StartAnimation -> startAnimation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (WhiteNoiseService.isRunning()) bindToService()
    }

    override fun onStop() {
        super.onStop()
        if (WhiteNoiseService.isRunning()) unBindFromService()
    }

    private fun startService(millis: Long, whiteNoise: WhiteNoise) {
        serviceIntent.apply {
            action = INIT_AND_START
            putExtra(MILLIS, millis)
            putExtra(WHITE_NOISE, whiteNoise)
        }
        requireContext().startService(serviceIntent)
    }

    private fun pauseService() {
        whiteNoiseService?.pause()
    }

    private fun resumeService() {
        whiteNoiseService?.resume()
    }

    private fun destroyService() {
        whiteNoiseService?.destroyService()
    }

    private fun bindToService() {
        requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_IMPORTANT)
        viewModel.isViewBindedToService = true
    }

    // TIL unbinding from a service you haven't binded to will crash the app -_-
    private fun unBindFromService() {
        requireContext().unbindService(serviceConnection)
        viewModel.isViewBindedToService = false
    }

    private fun onBind() {
        startAnimation()

        //shouldn't be null but just in case
        val whiteNoise: WhiteNoise = whiteNoiseService?.getWhiteNoise() ?: Rain()

        val indexOfItem = WhiteNoiseOptions.getIndexOfItemInList(whiteNoise)

        indexOfItem?.let { whiteNoiseRecyclerView.scrollToPosition(it) }

        zoomingLayoutManager.setScrollingEnabled(false)
    }

    private fun startAnimation() {
        TransitionManager.beginDelayedTransition(whiteNoiseFragment, transition)
        altConstraint.applyTo(whiteNoiseFragment)
    }

    private fun reverseAnimation() {
        TransitionManager.beginDelayedTransition(whiteNoiseFragment, transition)
        initialConstraints.applyTo(whiteNoiseFragment)
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            whiteNoiseService = (service as WhiteNoiseService.LocalBinder).getService()

            onBind()

            whiteNoiseService?.onServiceDestroyed = {
                lifecycleScope.launchWhenStarted {
                    //won't run until at least onStart has been called
                    if (viewModel.isViewBindedToService) unBindFromService()
                    reverseAnimation()
                    zoomingLayoutManager.setScrollingEnabled(true)
                    viewModel.resetState()
                    whiteNoiseService = null
                }
            }

            whiteNoiseService?.elapseTime?.observe(viewLifecycleOwner, Observer { elapseTime ->
                elapseTimeTextView.text = elapseTime.to24HourFormat()
            })

            whiteNoiseService?.isTimerRunning?.observe(viewLifecycleOwner, Observer { isRunning ->
                viewModel.setTimerActionIcon(isRunning)
            })
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    private fun openTimeOptionsDialog() {
        if (!timeSelectionDialog.isAdded)
            timeSelectionDialog.show(requireActivity().supportFragmentManager, "timeDialog")
    }
}
