package com.takari.sleeplock.feature.sleeptimer.ui

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
import com.takari.sleeplock.App
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.common.*
import com.takari.sleeplock.feature.sleeptimer.events.SleepTimerSingleEvent
import com.takari.sleeplock.feature.sleeptimer.events.SleepTimerViewEvent
import com.takari.sleeplock.feature.sleeptimer.service.SleepTimerService
import com.takari.sleeplock.feature.whitenoise.service.WhiteNoiseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.sleep_timer_fragment.*
import kotlin.math.log

class SleepTimerFragment : Fragment() {

    private val viewModel by activityViewModelFactory { App.applicationComponent.sleepTimerViewModel }
    private lateinit var sleepTimerService: SleepTimerService
    private val serviceIntent by lazy { Intent(context, SleepTimerService::class.java) }
    private val animate by lazy { Animate(context!!) }
    private val compositeDisposable = CompositeDisposable()
    private val timeOptionsDialog = TimeOptionsDialog()
    private val questionDialog = QuestionDialog()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sleep_timer_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPauseTimer.setOnClickListener { viewModel.viewEvent(SleepTimerViewEvent.OnStartPauseClick) }

        resetTimer.setOnClickListener { viewModel.viewEvent(SleepTimerViewEvent.OnResetClick) }

        sleepTimerFab.setOnClickListener {
            showDialog(
                timeOptionsDialog,
                activity!!.supportFragmentManager,
                "SleepTimerDialog"
            )
        }

        questionView.visibility = View.GONE
        questionView.setOnClickListener {
            showDialog(questionDialog, activity!!.supportFragmentManager, "Questions Dialog")
        }


        compositeDisposable += timeOptionsDialog.getUserSelectedTime().subscribeBy { millis ->
            viewModel.viewEvent(SleepTimerViewEvent.OnUserSelectsTime(millis))
        }

        compositeDisposable += questionDialog.getOnClick().subscribeBy {
            viewModel.viewEvent(SleepTimerViewEvent.OnQuestionDialogSuccessClick)
        }

        compositeDisposable += viewModel.state()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { state ->
                currentTimeTextView.text = state.currentTime.to24HourFormat()

                startPauseTimer.apply {
                    isEnabled = state.isEnabled
                    setBackgroundColor(Color.parseColor(state.color.hexCode))
                    setText(state.timerAction.name)
                }
            }

        compositeDisposable += viewModel.singleEvent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { singleEvent ->
                when (singleEvent) {

                    is SleepTimerSingleEvent.StartAnimation -> animate.start(
                        startPauseTimer,
                        resetTimer,
                        sleepTimerFab,
                        singleEvent.duration
                    )

                    is SleepTimerSingleEvent.ReverseAnimation -> animate.reset(
                        startPauseTimer,
                        resetTimer,
                        sleepTimerFab
                    )

                    is SleepTimerSingleEvent.HideQuestionView -> questionView.visibility = View.GONE

                    is SleepTimerSingleEvent.ShowQuestionView -> questionView.visibility = View.VISIBLE

                    is SleepTimerSingleEvent.ShowWarningToast -> context!!.showWarningToast(
                        singleEvent.message
                    )

                    is SleepTimerSingleEvent.OpenAdminActivity -> startActivity(singleEvent.intent)

                    is SleepTimerSingleEvent.StartService -> {
                        startService(context!!, singleEvent.millis)
                        bindToService(context!!)
                    }

                    is SleepTimerSingleEvent.PauseService -> pauseService()

                    is SleepTimerSingleEvent.ResumeService -> resumeService()

                    is SleepTimerSingleEvent.ResetService -> resetService()
                }
            }

        viewModel.viewEvent(SleepTimerViewEvent.OnCreateFinish)
    }

    override fun onStart() {
        super.onStart()
        if (SleepTimerService.isRunning()) bindToService(context!!)
        viewModel.viewEvent(SleepTimerViewEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        if (SleepTimerService.isRunning()) unBindFromService(context!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun startService(context: Context, millis: Long) {
        serviceIntent.apply {
            action = ServiceControls.Start.name
            putExtra(SleepTimerService.SLEEP_TIMER_TIME, millis)
        }
        context.startService(serviceIntent)
    }

    private fun pauseService() {
        sleepTimerService.pause()
    }

    private fun resumeService() {
        sleepTimerService.resume()
    }

    private fun resetService() {
        sleepTimerService.reset()
    }

    private fun bindToService(context: Context) {
        context.bindService(serviceIntent, serviceConnection, 0)
    }

    private fun unBindFromService(context: Context) {
        context.unbindService(serviceConnection)
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            sleepTimerService = (service as SleepTimerService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}
