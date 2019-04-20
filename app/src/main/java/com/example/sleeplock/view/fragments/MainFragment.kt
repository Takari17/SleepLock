package com.example.sleeplock.view.fragments


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.sleeplock.R
import com.example.sleeplock.model.util.DataSource
import com.example.sleeplock.model.service.TimerBroadcastReceiver
import com.example.sleeplock.model.service.isServiceRunning
import com.example.sleeplock.view.Animate
import com.example.sleeplock.view.TimeOptionDialog
import com.example.sleeplock.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.fragment_main.*

// todo apply a dark theme, maybe change from light blue to something more mellow


// todo create private setters for your live data
// todo see if you can move your broadcast recceiver into your vm
class MainFragment : Fragment() {

    private lateinit var viewModel: MyViewModel
    private val dataSource: DataSource = DataSource()
    private lateinit var timerBroadcastReceiver: TimerBroadcastReceiver

    private val animate = Animate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerBroadcastReceiver = TimerBroadcastReceiver(context!!)
        timerBroadcastReceiver.registerBroadcast()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(MyViewModel::class.java)
        observeAllLiveData()

        fab.setOnClickListener { showDialog() }


        start_pause_button.setOnClickListener {
            // todo move this business logic into your view model if you can
            if (viewModel.startButton) {

                viewModel.startButtonClick(viewModel.startButton)

                startAnimation()

            } else {
                viewModel.pauseButtonClick(viewModel.startButton)
            }
        }

        reset_button.setOnClickListener { viewModel.resetButtonClick() }

    }

    private fun showDialog() {
        val dialog = TimeOptionDialog()
        viewModel.subscribeToDialog(dialog.dialogTime) // todo we can just use live data for this

        if (!dialog.isAdded) {
            val fragmentManager: FragmentManager? = activity?.supportFragmentManager
            if (fragmentManager != null) dialog.show(fragmentManager, "Time Dialog")

       }
    }


    private fun updateCardViewData(index: Int, dataSource: DataSource) {
        Glide.with(context!!)
            .asBitmap()
            .load(dataSource.ITEM_PIC[index])
            .into(card_view_pic)

        card_view_text.text = dataSource.ITEM_TEXT[index]
    }

    private fun resetCardViewData() {
        Glide.with(context!!)
            .asBitmap()
            .load(R.drawable.nosound)
            .into(card_view_pic)

        card_view_text.text = R.string.no_sound.toString()
    }


    override fun onPause() {
        super.onPause()
        viewModel.maybeStartService()
    }


    override fun onResume() {
        super.onResume()
        viewModel.destroyService()

        if (isServiceRunning) {

            startAnimation(0) // animation is instant
            viewModel.restoreButton()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerBroadcastReceiver.unregisterBroadcast()
    }

    // Live Data Observers
    private fun observeCurrentTime(): Observer<String> { // Updates our text view with the current time
        return Observer { time -> current_time_text_view.text = time }
    }

    private fun observeItemIndex(): Observer<Int> { // sets card view text and image to the item selected in the recycler view
        return Observer { index -> updateCardViewData(index, dataSource) }
    }

    private fun observeButtonColor(): Observer<Int> { // sets the button color
        return Observer { color -> start_pause_button.setTextColor(color) }
    }

    private fun observeEnabledDisabled(): Observer<Boolean> { // Sets button clickability
        return Observer { aBoolean -> start_pause_button.isClickable = aBoolean }
    }

    private fun observeButtonText(): Observer<String> { // Sets button text
        return Observer { text -> start_pause_button.text = text }
    }

    private fun observeTimerCompleted(): Observer<Boolean> {
        return Observer {
            reverseAnimation()
            resetCardViewData()
        }
    }

    private fun observeBroadcastTime(): Observer<Long> {
        return Observer {millis ->
            viewModel.createAndObserveTimer(millis!!)
            viewModel.startTimer()        }
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

    private fun observeAllLiveData() {
        viewModel.updateCurrentTime.observe(viewLifecycleOwner, observeCurrentTime())

        viewModel.clickedItemIndex.observe(viewLifecycleOwner, observeItemIndex())

        viewModel.enabledOrDisabled.observe(viewLifecycleOwner, observeEnabledDisabled())

        viewModel.updateButtonColor.observe(viewLifecycleOwner, observeButtonColor())

        viewModel.updateButtonText.observe(viewLifecycleOwner, observeButtonText())

        viewModel.timerCompleted.observe(viewLifecycleOwner, observeTimerCompleted())

        timerBroadcastReceiver.broadcastTime.observe(viewLifecycleOwner, observeBroadcastTime())
    }
}
