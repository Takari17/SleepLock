package com.example.sleeplock.injection

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/*
Higher order extension function that allows dagger to inject viewModels.

Scoped to the underlying activity, not the fragment itsself
 */
inline fun <reified T : ViewModel> Fragment.activityViewModelFactory(
    crossinline provider: () -> T
) = activityViewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider() as T
    }
}