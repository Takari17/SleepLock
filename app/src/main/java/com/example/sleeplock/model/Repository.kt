package com.example.sleeplock.model

import android.app.Application
import android.content.Intent
import com.example.sleeplock.model.service.CustomService
import com.example.sleeplock.model.util.Constants
import com.example.sleeplock.model.util.Constants.ACTION_PLAY
import com.example.sleeplock.model.util.Constants.ACTION_RESET

class Repository(private val application: Application) {

    private val serviceIntent: Intent = Intent(application, CustomService::class.java)

    fun startService(millis: Long) {
        serviceIntent.action = ACTION_PLAY.text
        serviceIntent.putExtra(Constants.CURRENT_TIME.text, millis)
        application.startService(serviceIntent)
    }

    fun resetService() {
        serviceIntent.action = ACTION_RESET.text
        application.startService(serviceIntent)
    }
}
