package com.example.sleeplock

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.Constants.*


//todo: clear the card view image when the timer isn't running and we close the app

// Handles the service & broadcast
class Repository(private val application: Application) {

    private val serviceIntent: Intent = Intent(application, CustomService::class.java)
    val passTime = MutableLiveData<Long>()

    init {
        val receiver = Receiver(passTime)
        application.registerReceiver(receiver, IntentFilter(MILLIS.text))
    }


    fun startService(millis: Long) {
        serviceIntent.action = ACTION_PLAY.text
        serviceIntent.putExtra(Constants.CURRENT_TIME.text, millis)
        application.startService(serviceIntent)
    }



    fun resetService() {
        serviceIntent.action = ACTION_RESET.text
        application.startService(serviceIntent)
    }


    // Broadcast receiver that'll notify our view model
    class Receiver(private val passTime: MutableLiveData<Long>) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val millis = intent?.getLongExtra("millis", 0)
            passTime.postValue(millis)
        }
    }


}
