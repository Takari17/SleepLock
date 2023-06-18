package com.takari.sleeplock.sleeptimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class SleepTimerFragment : Fragment() {

    companion object {
        const val TAG = "Sleep Timer"
    }

    private lateinit var viewModel: SleepTimerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SleepTimerViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent { SleepTimerScreen(viewModel) }
        }
    }

}