package com.takari.sleeplock.whitenoise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseViewCommands
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseViewModel




class WhiteNoiseFragment : Fragment() {
    companion object {
        const val TAG = "White Noise"
    }

    private lateinit var viewModel: WhiteNoiseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[WhiteNoiseViewModel::class.java]

        viewModel.viewCommand = { command ->
            val placeHolder = when (command) {
                is WhiteNoiseViewCommands.StartAndBindToService -> {}
                is WhiteNoiseViewCommands.PauseService -> {}
                is WhiteNoiseViewCommands.ResumeService -> {}
                is WhiteNoiseViewCommands.DestroyService -> {}
                is WhiteNoiseViewCommands.OpenTimeSelectionDialog -> {}
                // TODO we can have a boolean in a data class for this. Than use a simple if else statement
                is WhiteNoiseViewCommands.StartAnimation -> {}
            }
        }

        return ComposeView(requireContext()).apply {
            setContent { WhiteNoiseScreen(viewModel) }
        }
    }
}