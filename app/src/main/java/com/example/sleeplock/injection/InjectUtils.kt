package com.example.sleeplock.injection

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/*
Higher Order Function that takes a view model and instantiates it
 */

inline fun <reified T : ViewModel> FragmentActivity.injectViewModel(
    crossinline provider: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider() as T
    }
}