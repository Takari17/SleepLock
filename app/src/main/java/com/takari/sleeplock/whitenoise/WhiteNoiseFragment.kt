package com.takari.sleeplock.whitenoise

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
import com.takari.sleeplock.whitenoise.data.WhiteNoise
import com.takari.sleeplock.whitenoise.service.WhiteNoiseService
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseOneTimeEvents
import kotlinx.coroutines.launch




/*




class WhiteNoiseFragment : Fragment() {
    companion object {
        const val TAG = "White Noise"
    }

    private var whiteNoiseService: WhiteNoiseService? = null
    private var bindedToService: Boolean = false
    private lateinit var viewModel: WhiteNoiseViewModel

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logD("WhiteNoiseFragment binded")
            bindedToService = true

            whiteNoiseService = (service as WhiteNoiseService.LocalBinder).getService()

            lifecycleScope.launch {
                whiteNoiseService!!.timerFlow.get
                    .collect { timerState -> viewModel.setTimerState(timerState) }
            }

//            restoreRowState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logD("WhiteNoiseFragment disconnected")

            bindedToService = false


//            whiteNoiseService?.onDestroyed = {
//                viewLifecycleOwner.lifecycleScope.launch {
//                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                    //TODO why do we need this? Why not just use the onServiceDisconnected callback? This is redundant.
//                    //won't run until at least onStart has been called
//                    if (bindedToService) {
//                        unBindFromService()
//                    }
//
////                    reverseAnimation()
////                    zoomingLayoutManager.setScrollingEnabled(true)
////                    viewModel.resetState()
////                    whiteNoiseService = null
//                }}

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[WhiteNoiseViewModel::class.java]

        viewModel.events = { command ->
            logD(command.toString())

            when (command) {
                is WhiteNoiseOneTimeEvents.StartAndBindToService -> {
                    startService(command.millis, command.whiteNoise)
                    bindToService()
                }

                is WhiteNoiseOneTimeEvents.PauseService -> whiteNoiseService?.pause()
                is WhiteNoiseOneTimeEvents.ResumeService -> whiteNoiseService?.resume()
                is WhiteNoiseOneTimeEvents.DestroyService -> whiteNoiseService?.destroyService()
            }
        }

        return ComposeView(requireContext()).apply {
            setContent { WhiteNoiseScreen(viewModel) }
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
        val serviceIntent = Intent(context, WhiteNoiseService::class.java)

        serviceIntent.apply {
            action = WhiteNoiseService.INIT_AND_START
            putExtra(WhiteNoiseService.MILLIS, millis)
            putExtra(WhiteNoiseService.WHITE_NOISE, whiteNoise)
        }

        requireContext().startService(serviceIntent)
    }

    private fun bindToService() {
        requireContext().bindService(
            Intent(context, WhiteNoiseService::class.java),
            connection,
            Context.BIND_IMPORTANT
        )
    }

    private fun unBindFromService() {
        requireContext().unbindService(connection)
    }

    private fun restoreRowState() {
//        val whiteNoise: WhiteNoise = whiteNoiseService?.getWhiteNoise() ?: Rain()
//        val indexOfItem = WhiteNoiseOptions.getIndexOfItemInList(whiteNoise)
//        indexOfItem?.let { whiteNoiseRecyclerView.scrollToPosition(it) }
//        zoomingLayoutManager.setScrollingEnabled(false)
    }
}