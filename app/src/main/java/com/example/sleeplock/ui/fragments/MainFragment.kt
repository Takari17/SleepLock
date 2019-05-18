package com.example.sleeplock.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.sleeplock.R
import com.example.sleeplock.injection.Application.Companion.applicationComponent
import com.example.sleeplock.injection.activityViewModelFactory
import com.example.sleeplock.ui.common.Animate
import com.example.sleeplock.ui.common.TimeOptionDialog
import com.example.sleeplock.utils.ITEM_PIC
import com.example.sleeplock.utils.ITEM_TEXT
import com.example.sleeplock.utils.formatTime
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    private val viewModel by activityViewModelFactory { applicationComponent.mainViewModel }
    private val dialog = TimeOptionDialog()
    private val animate = Animate()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAllLiveData()

        fab.setOnClickListener { showDialog() }

        start_pause_button.setOnClickListener {

            if (viewModel.startButtonClicked) {
                viewModel.startPauseButtonClick(true)
                startAnimation()

            } else viewModel.startPauseButtonClick(false)
        }

        reset_button.setOnClickListener { viewModel.resetButtonClick() }

        viewModel.fragmentActivityCreated()
    }

    private fun showDialog() {
        if (!dialog.isAdded) {
            val fragmentManager: FragmentManager? = activity?.supportFragmentManager
            if (fragmentManager != null) dialog.show(fragmentManager, "Time Dialog")
        }
    }

    private fun updateCardViewData(index: Int) {
        Glide.with(context!!)
            .asBitmap()
            .load(ITEM_PIC[index])
            .into(card_view_pic)

        card_view_text.text = ITEM_TEXT[index]
    }

    private fun resetCardViewData() {
        Glide.with(context!!)
            .asBitmap()
            .load(R.drawable.nosound)
            .into(card_view_pic)

        card_view_text.text = resources.getString(R.string.no_sound)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //todo shoudnt this be onStart?
        viewModel.bindToServiceIfRunning()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onFragmentStart()
    }

    private fun startAnimation(duration: Long = 500) {
        if (!viewModel.reverseAnim) {
            animate.translateAll(start_pause_button, reset_button, fab, duration)
            animate.fadeInAll(reset_button, fab, duration)
            viewModel.reverseAnim = !viewModel.reverseAnim
        }
    }

    private fun reverseAnimation() {
        // called when timer finishes
        animate.reverseTranslateAll(start_pause_button, reset_button, fab)
        animate.fadeOutAll(reset_button, fab)
        viewModel.reverseAnim = !viewModel.reverseAnim
    }

    // Live Data Observers

    private fun observeCurrentTime(): Observer<Long> { // Updates our text view with the current time
        return Observer { millis -> current_time_text_view.text = millis.formatTime() }
    }

    private fun observeTimerPaused(): Observer<Boolean> {
        return Observer { viewModel.setButtonText(false) }
    }

    private fun observeTimerStarted(): Observer<Boolean> {
        return Observer { viewModel.setButtonText(true) }
    }

    private fun observeUserSelectedTime(): Observer<Long> { // Time chosen from dialog
        return Observer { millis ->
            current_time_text_view.text = millis.formatTime()
            viewModel.passDialogTime(millis)
        }
    }

    private fun observeItemIndex(): Observer<Int> { // sets card view text and image to the item selected in the recycler view
        return Observer { index ->
            index?.let {
                updateCardViewData(index)
            }
        }
    }

    private fun observeButtonColor(): Observer<Int> { // sets the button color
        return Observer { color -> start_pause_button.setTextColor(color) }
    }

    private fun observeEnabledDisabled(): Observer<Boolean> { // Sets button clickability
        return Observer { aBoolean -> start_pause_button.isEnabled = aBoolean }
    }

    private fun observeButtonText(): Observer<String> { // Sets button text
        return Observer { text -> start_pause_button.text = text }
    }

    private fun observeTimerCompleted(): Observer<Boolean> {
        return Observer {
            reverseAnimation()
            resetCardViewData()
            viewModel.resetAll()
        }
    }

    private fun observeAnimation(): Observer<Long> =
        Observer { duration ->
            startAnimation(duration) // animation is instant
        }

    private fun observeAllLiveData() {
        viewModel.getCurrentTime().observe(viewLifecycleOwner, observeCurrentTime())

        viewModel.getClickedItemIndex().observe(viewLifecycleOwner, observeItemIndex())

        viewModel.getButtonEnabled().observe(viewLifecycleOwner, observeEnabledDisabled())

        viewModel.getButtonColor().observe(viewLifecycleOwner, observeButtonColor())

        viewModel.getButtonText().observe(viewLifecycleOwner, observeButtonText())

        viewModel.getTimerStarted().observe(viewLifecycleOwner, observeTimerStarted())

        viewModel.getTimerPaused().observe(viewLifecycleOwner, observeTimerPaused())

        viewModel.getTimerCompleted().observe(viewLifecycleOwner, observeTimerCompleted())

        viewModel.getStartAnimation().observe(viewLifecycleOwner, observeAnimation())

        dialog.getUserSelectedTime().observe(viewLifecycleOwner, observeUserSelectedTime())
    }
}
