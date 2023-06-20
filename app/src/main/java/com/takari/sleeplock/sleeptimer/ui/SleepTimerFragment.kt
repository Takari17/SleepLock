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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.takari.sleeplock.log
import com.takari.sleeplock.sleeptimer.permissions.AdminPermissionManager
import com.takari.sleeplock.sleeptimer.service.SleepTimerService
import com.takari.sleeplock.to24HourFormat
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


class SleepTimerFragment : Fragment() {

    companion object {
        const val TAG = "Sleep Timer"
    }

    @Inject
    lateinit var permissionManager: AdminPermissionManager
    private var sleepTimerService: SleepTimerService? = null
    private lateinit var viewModel: SleepTimerViewModel

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logD("SleepTimerFragment binded to service.")

            sleepTimerService = (service as SleepTimerService.LocalBinder).getService()

            lifecycleScope.launch {
                sleepTimerService!!.timerFlow.get.collect { timerState ->
                    viewModel.setTimerState(
                        timerState
                    )
                }
            }

            restoreState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            log("SleepTimerFragment unbinded to service.")
            viewModel.resetState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!permissionManager.permissionIsEnabled()) {
            permissionManager.requestPermissions(this) { isGranted ->
                if (!isGranted) requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SleepTimerViewModel::class.java]

        viewModel.events = { command ->
            log(command.toString())

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
            Intent(context, SleepTimerService::class.java), connection, Context.BIND_IMPORTANT
        )
    }

    private fun unBindFromService() {
        requireContext().unbindService(connection)
    }

    private fun restoreState() {
        viewModel.restoreState(
            SleepTimerUiState(
                timerServiceIsRunning = SleepTimerService.isRunning(),
                isTimerRunning = SleepTimerService.timerIsRunning(),
                elapseTime = sleepTimerService!!.timerFlow.get.value.elapseTime.to24HourFormat(),
            )
        )
    }
}