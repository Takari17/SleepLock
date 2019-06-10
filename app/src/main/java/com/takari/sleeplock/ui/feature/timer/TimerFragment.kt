package com.takari.sleeplock.ui.feature.timer


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.takari.sleeplock.Application.Companion.applicationComponent
import com.takari.sleeplock.R
import com.takari.sleeplock.data.service.MainService
import com.takari.sleeplock.ui.common.Animate
import com.takari.sleeplock.ui.common.TimeOptionDialog
import com.takari.sleeplock.utils.activityViewModelFactory
import com.takari.sleeplock.utils.formatTime
import kotlinx.android.synthetic.main.fragment_main.*

class TimerFragment : Fragment() {

    private val viewModel by activityViewModelFactory { applicationComponent.timerViewModel }
    private val dialog = TimeOptionDialog()
    private val animate by lazy { Animate(context!!) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_main, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (MainService.isRunning) startAnimation(Animate.INSTANT)

        observeViewModelLiveData()

        dialog.getUserSelectedTime().observe(viewLifecycleOwner, observeUserSelectedTime())

        fab.setOnClickListener { showDialog() }

        startPauseButton.setOnClickListener {
            viewModel.startPauseButtonClick(
                viewModel.isTimerRunning.value ?: false
            )
        }

        resetButton.setOnClickListener { viewModel.resetButtonClick() }
    }

    private fun showDialog() {
        if (!dialog.isAdded) {
            val fragmentManager: FragmentManager? = activity?.supportFragmentManager
            if (fragmentManager != null) dialog.show(fragmentManager, "Time Dialog")
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.mainFragmentOnStart()
    }

    override fun onStop() {
        super.onStop()
        viewModel.mainFragmentOnStop()
    }

    private fun startAnimation(duration: Long = 500) {
        animate.translateAll(startPauseButton, resetButton, fab, duration)
        animate.fadeInAll(resetButton, fab, duration)
    }

    private fun reverseAnimation() {
        animate.reverseTranslateAll(startPauseButton, resetButton, fab)
        animate.fadeOutAll(resetButton, fab)
    }


    private fun observeViewModelLiveData() = viewModel.apply {
        currentTime.observe(viewLifecycleOwner, observeCurrentTime())

        isTimerRunning.observe(viewLifecycleOwner, observeIsTimerRunning())

        getCardViewImage().observe(viewLifecycleOwner, observeCardViewImage())

        getCardViewText().observe(viewLifecycleOwner, observeCardViewText())

        getButtonEnabled().observe(viewLifecycleOwner, observeEnabledDisabled())

        getButtonColor().observe(viewLifecycleOwner, observeButtonColor())

        getButtonText().observe(viewLifecycleOwner, observeButtonText())

        getStartAnimation().observe(viewLifecycleOwner, observeStartAnimation())

        getReverseAnimation().observe(viewLifecycleOwner, observeReverseAnimation())
    }

    //Live Data Observers
    private fun observeCurrentTime() =
        Observer<Long> { millis -> currentTimeTextView.text = millis.formatTime() }


    private fun observeIsTimerRunning() =
        Observer<Boolean?> { isRunning ->
            isRunning?.let {
                viewModel.setButtonText(isRunning)
            }
        }

    private fun observeUserSelectedTime() =
        Observer<Long> { millis ->
            currentTimeTextView.text = millis.formatTime()
            viewModel.passDialogTime(millis)
        }

    private fun observeCardViewImage() =
        Observer<Int?> { drawable ->
            drawable?.let {
                Glide.with(context!!)
                    .asBitmap()
                    .load(drawable)
                    .into(cardViewPic)
            }
        }

    private fun observeCardViewText() =
        Observer<String?> { newText ->
            newText?.let {
                cardViewText.text = newText
            }
        }

    private fun observeStartAnimation() =
        Observer<Long> { duration -> startAnimation(duration) }

    private fun observeReverseAnimation() =
        Observer<Boolean> { reverseAnimation() }

    private fun observeButtonColor() =
        Observer<Int?> { color -> color?.let { startPauseButton.setTextColor(color) } }

    private fun observeEnabledDisabled() =
        Observer<Boolean?> { shouldEnable -> shouldEnable?.let { startPauseButton.isEnabled = shouldEnable } }

    private fun observeButtonText() =
        Observer<String?> { text -> text?.let { startPauseButton.text = text } }
}