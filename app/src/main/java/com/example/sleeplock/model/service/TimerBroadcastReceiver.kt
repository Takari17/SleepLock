package com.example.sleeplock.model.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sleeplock.model.util.Constants

class TimerBroadcastReceiver(context: Context) {

    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    val broadcastTime = MutableLiveData<Long>()

    fun registerBroadcast(){
        localBroadcastManager.registerReceiver(receiver, IntentFilter(Constants.MILLIS.text))
    }

    fun unregisterBroadcast(){
        localBroadcastManager.unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val millis = intent?.getLongExtra("millis", 0)
            broadcastTime.value = millis

        }
    }
}