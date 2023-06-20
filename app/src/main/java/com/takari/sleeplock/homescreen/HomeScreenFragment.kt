package com.takari.sleeplock.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.takari.sleeplock.homescreen.HomeScreen
import com.takari.sleeplock.shared.theme.SleepLockTheme

class HomeScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SleepLockTheme {
                    HomeScreen(requireActivity())
                }
            }
        }
    }
}