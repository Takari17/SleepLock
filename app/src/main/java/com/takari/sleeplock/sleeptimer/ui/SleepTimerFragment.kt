package com.takari.sleeplock.sleeptimer.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.takari.sleeplock.logD
import com.takari.sleeplock.sleeptimer.service.SleepTimerService
import kotlinx.coroutines.launch

class SleepTimerFragment : Fragment() {

    companion object {
        const val TAG = "Sleep Timer"
    }

    private var sleepTimerService: SleepTimerService? = null
    private lateinit var viewModel: SleepTimerViewModel

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logD("SleepTimerFragment binded to service.")

            sleepTimerService = (service as SleepTimerService.LocalBinder).getService()

            lifecycleScope.launch {
                sleepTimerService!!.timerFlow.get
                    .collect { timerState -> viewModel.setTimerState(timerState) }
            }

//            restoreState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logD("SleepTimerFragment unbinded to service.")
            viewModel.resetState()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SleepTimerViewModel::class.java]

        viewModel.events = { command ->
            logD(command.toString())

            when (command) {
                is SleepTimerViewCommands.StartAndBindToService -> {
                    startService(command.millis)
                    bindToService()
                }

                is SleepTimerViewCommands.PauseService -> sleepTimerService?.pause()
                is SleepTimerViewCommands.ResumeService -> sleepTimerService?.resume()
                is SleepTimerViewCommands.DestroyService -> sleepTimerService?.destroyService()
            }
        }

        return ComposeView(requireContext()).apply {
            setContent { SleepTimerScreen(viewModel) }
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

    private fun startService(millis: Long) {
        val serviceIntent = Intent(context, SleepTimerService::class.java)

        serviceIntent.apply {
            action = SleepTimerService.START
            putExtra(SleepTimerService.MILLIS, millis)
        }

        requireContext().startService(serviceIntent)
    }

    private fun bindToService() {
        requireContext().bindService(
            Intent(context, SleepTimerService::class.java),
            connection,
            Context.BIND_IMPORTANT
        )
    }

    private fun unBindFromService() {
        requireContext().unbindService(connection)
    }

    private fun restoreState() {

    }
}