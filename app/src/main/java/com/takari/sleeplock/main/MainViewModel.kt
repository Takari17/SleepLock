package com.takari.sleeplock.main

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    var switchContainers: (FragmentName) -> Unit = {}

}