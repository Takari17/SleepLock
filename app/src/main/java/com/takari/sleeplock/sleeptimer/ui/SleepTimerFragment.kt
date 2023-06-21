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
import com.takari.sleeplock.shared.TimeSelectionDialog
import com.takari.sleeplock.shared.log
import com.takari.sleeplock.shared.to24HourFormat
import com.takari.sleeplock.sleeptimer.permissions.AdminPermissionManager
import com.takari.sleeplock.sleeptimer.service.SleepTimerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SleepTimerFragment : Fragment() {

    companion object {
        const val TAG = "Sleep Timer"
    }

    @Inject
    lateinit var permissionManager: AdminPermissionManager
    private var sleepTimerService: SleepTimerService? = null
    private lateinit var viewModel: SleepTimerViewModel
    private val timeSelectionDialog = TimeSelectionDialog()

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            log("SleepTimerFragment binded to service.")

            sleepTimerService = (service as SleepTimerService.LocalBinder).getService()

            lifecycleScope.launch {
                sleepTimerService!!.timerFlow.elapseTime
                    .collect { elapseTime -> viewModel.setElapseTime(elapseTime) }
            }

            lifecycleScope.launch {
                sleepTimerService!!.timerFlow.isRunning
                    .collect { isRunning -> viewModel.setIsTimerRunning(isRunning) }
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
                is SleepTimerViewCommands.ShowTimePickerDialog -> openTimeOptionsDialog()
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
                elapseTime = sleepTimerService!!.timerFlow.elapseTime.value.to24HourFormat(),
            )
        )
    }

    private fun openTimeOptionsDialog() {

        timeSelectionDialog.onTimeSelected = { millis: Long ->
            viewModel.onUserSelectedTimeFromDialog(millis)
        }

        if (!timeSelectionDialog.isAdded) {
            timeSelectionDialog.show(requireActivity().supportFragmentManager, "timeDialog")
        }
    }
}