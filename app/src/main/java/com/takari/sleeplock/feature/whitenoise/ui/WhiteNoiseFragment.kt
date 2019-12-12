package com.takari.sleeplock.feature.whitenoise.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.api.load
import com.takari.sleeplock.App.Companion.applicationComponent
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.common.*
import com.takari.sleeplock.feature.whitenoise.data.sounds.WhiteNoise
import com.takari.sleeplock.feature.whitenoise.events.WhiteNoiseSingleEvent
import com.takari.sleeplock.feature.whitenoise.events.WhiteNoiseViewEvent
import com.takari.sleeplock.feature.whitenoise.service.WhiteNoiseService
import com.takari.sleeplock.feature.whitenoise.service.WhiteNoiseService.Companion.WHITE_NOISE
import com.takari.sleeplock.feature.whitenoise.service.WhiteNoiseService.Companion.WHITE_NOISE_TIME
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.white_noise_fragment.*

class WhiteNoiseFragment : Fragment() {

    private val viewModel by activityViewModelFactory { applicationComponent.whiteNoiseViewModel }
    private val timeOptionDialog = TimeOptionsDialog()
    private val soundOptionDialog = SoundOptionsDialog()
    private lateinit var whiteNoiseService: WhiteNoiseService
    private val serviceIntent by lazy { Intent(context, WhiteNoiseService::class.java) }
    private val compositeDisposable = CompositeDisposable()
    private val animate by lazy { Animate(context!!) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.white_noise_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPauseWhiteNoise.setOnClickListener { viewModel.viewEvent(WhiteNoiseViewEvent.OnStartPauseClick) }

        resetWhiteNoise.setOnClickListener { whiteNoiseService.reset() }

        whiteNoiseCardView.setOnClickListener { viewModel.viewEvent(WhiteNoiseViewEvent.OnOpenWhiteNoiseOptions) }

        whiteNoiseFab.setOnClickListener {
            showDialog(
                timeOptionDialog,
                activity!!.supportFragmentManager,
                "Time Dialog"
            )
        }

        compositeDisposable += timeOptionDialog.getUserSelectedTime().subscribeBy { millis ->
            viewModel.viewEvent(WhiteNoiseViewEvent.OnUserSelectsTime(millis)) }

        compositeDisposable += soundOptionDialog.getWhiteNoiseData().subscribeBy { whiteNoise ->
            viewModel.viewEvent(WhiteNoiseViewEvent.OnUserSelectsWhiteNoise(whiteNoise)) }

        compositeDisposable += viewModel.state()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { state ->
                if (state.whiteNoise != null) {
                    whiteNoiseImage.load(state.whiteNoise.image())
                    whiteNoiseText.text = state.whiteNoise.name()
                } else {
                    whiteNoiseImage.load(R.drawable.nosound)
                    whiteNoiseText.text = "Select White Noise"
                }

                currentTimeTextView.text = state.currentTime.to24HourFormat()

                startPauseWhiteNoise.apply {
                    isEnabled = state.isEnabled
                    setBackgroundColor(Color.parseColor(state.color.hexCode))
                    setText(state.timerAction.name)
                }
            }

        compositeDisposable += viewModel.singleEvent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { singleEvent ->
                when (singleEvent) {
                    is WhiteNoiseSingleEvent.StartAnimation -> animate.start(
                        startPauseWhiteNoise,
                        resetWhiteNoise,
                        whiteNoiseFab,
                        singleEvent.duration
                    )

                    is WhiteNoiseSingleEvent.ReverseAnimation -> animate.reset(
                        startPauseWhiteNoise,
                        resetWhiteNoise,
                        whiteNoiseFab
                    )

                    is WhiteNoiseSingleEvent.OpenSoundDialog -> showDialog(
                        soundOptionDialog,
                        activity!!.supportFragmentManager,
                        "Sound Dialog"
                    )

                    is WhiteNoiseSingleEvent.ShowWarningToast -> context!!.showWarningToast(singleEvent.message)

                    is WhiteNoiseSingleEvent.StartService -> {
                        startService(context!!, singleEvent.millis, singleEvent.whiteNoise)
                        bindToService(context!!)
                    }

                    is WhiteNoiseSingleEvent.PauseService -> pauseService()

                    is WhiteNoiseSingleEvent.ResumeService -> resumeService()

                    is WhiteNoiseSingleEvent.ResetService -> resetService()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (WhiteNoiseService.isRunning()) bindToService(context!!)
        viewModel.viewEvent(WhiteNoiseViewEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        if (WhiteNoiseService.isRunning()) unBindFromService(context!!)
        viewModel.viewEvent(WhiteNoiseViewEvent.OnStop)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun startService(context: Context, millis: Long, whiteNoise: WhiteNoise) {
        serviceIntent.apply {
            action = ServiceControls.Start.name
            putExtra(WHITE_NOISE_TIME, millis)
            putExtra(WHITE_NOISE, whiteNoise)
        }
        context.startService(serviceIntent)
    }

    private fun pauseService() {
        whiteNoiseService.pause()
    }

    private fun resumeService() {
        whiteNoiseService.resume()
    }

    private fun resetService() {
        whiteNoiseService.reset()
    }

    private fun bindToService(context: Context) {
        context.bindService(serviceIntent, serviceConnection, 0)
    }

    private fun unBindFromService(context: Context) {
        context.unbindService(serviceConnection)
    }


    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            whiteNoiseService = (service as WhiteNoiseService.LocalBinder).getService()

        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}
