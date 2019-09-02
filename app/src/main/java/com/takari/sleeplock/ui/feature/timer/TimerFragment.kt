package com.takari.sleeplock.ui.feature.timer


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import coil.api.load
import com.takari.sleeplock.App.Companion.applicationComponent
import com.takari.sleeplock.R
import com.takari.sleeplock.ui.common.Animate
import com.takari.sleeplock.ui.common.TimeOptionDialog
import com.takari.sleeplock.utils.activityViewModelFactory
import com.takari.sleeplock.utils.formatTime
import kotlinx.android.synthetic.main.timer_fragment.*


class TimerFragment : Fragment() {
    
    private val sharedViewModel by activityViewModelFactory { applicationComponent.sharedViewModel }
    private val timeOptionsDialog = TimeOptionDialog()
    private val animate by lazy { Animate(context!!) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.timer_fragment, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener { showDialog() }
        startPauseButton.setOnClickListener { sharedViewModel.startOrPauseTimer() }
        resetButton.setOnClickListener { sharedViewModel.resetSoundAndTimer() }


        sharedViewModel.getCurrentTime().observe(viewLifecycleOwner, Observer { millis ->
            currentTimeTextView.text = millis.formatTime()
        })


        sharedViewModel.getTimerCompleted().observe(viewLifecycleOwner, Observer {
            reverseAnimation()
        })


        sharedViewModel.getButtonState().observe(viewLifecycleOwner, Observer { state ->
            startPauseButton.isEnabled = state.enabled
            startPauseButton.setTextColor(Color.parseColor(state.color))
        })


        sharedViewModel.getTimerAction().observe(viewLifecycleOwner, Observer { action ->
            startPauseButton.text = action
        })


        sharedViewModel.getAnimate().observe(viewLifecycleOwner, Observer { duration ->
            startAnimation(duration)
        })


        sharedViewModel.getWhiteNoiseData().observe(viewLifecycleOwner, Observer { whiteNoise ->
            cardViewPic.load(whiteNoise.image)
            cardViewText.text = whiteNoise.name
        })


        timeOptionsDialog.getUserSelectedTime().observe(viewLifecycleOwner, Observer { chosenTimeInMillis ->
            currentTimeTextView.text = chosenTimeInMillis.formatTime()
            sharedViewModel.setTime(chosenTimeInMillis)
        })
    }

    private fun showDialog() {
        if (!timeOptionsDialog.isAdded) {
            val fragmentManager: FragmentManager? = activity?.supportFragmentManager
            if (fragmentManager != null) timeOptionsDialog.show(fragmentManager, "Time Dialog")
        }
    }

    private fun startAnimation(duration: Long = 500) {
        animate.translateAll(startPauseButton, resetButton, fab, duration)
        animate.fadeInAll(resetButton, fab, duration)
    }

    private fun reverseAnimation() {
        animate.reverseTranslateAll(startPauseButton, resetButton, fab)
        animate.fadeOutAll(resetButton, fab)
    }
}

/**
 * Represents the state of startPause button.
 */
data class ButtonState(
    val enabled: Boolean,
    val color: String
)