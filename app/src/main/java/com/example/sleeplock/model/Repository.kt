package com.example.sleeplock.model

import android.app.Application
import android.content.Intent
import com.example.sleeplock.model.service.MyService
import com.example.sleeplock.utils.ACTION_FORCE_STOP
import com.example.sleeplock.utils.ACTION_PLAY
import com.example.sleeplock.utils.ACTION_RESET
import com.example.sleeplock.utils.CURRENT_TIME

class Repository(private val application: Application) {

    private val serviceIntent: Intent = Intent(application, MyService::class.java)

    fun startService(millis: Long) {
        serviceIntent.action = ACTION_PLAY
        serviceIntent.putExtra(CURRENT_TIME, millis)
        application.startService(serviceIntent)
    }

    fun resetService() {
        serviceIntent.action = ACTION_FORCE_STOP
        application.startService(serviceIntent)
    }
}
