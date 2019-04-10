package com.example.sleeplock

import android.app.Application
import android.content.Intent
import android.util.Log

import com.example.sleeplock.Constants.ACTION_PLAY
import com.example.sleeplock.Constants.ACTION_PAUSE
import com.example.sleeplock.Constants.ACTION_RESET

class Repository(private val application: Application) {

    private val serviceIntent: Intent = Intent(application, CustomService::class.java)
    private val saveData = SaveData(application)


    fun startService(millis: Long){
        serviceIntent.action = ACTION_PLAY.text
        serviceIntent.putExtra(Constants.CURRENT_TIME.text, millis)
        application.startService(serviceIntent)
        Log.d("mylog", " start service ran")
    }

    fun pauseService(){
        serviceIntent.action = ACTION_PAUSE.text
        application.startService(serviceIntent)

    }

    fun resetService(){
        serviceIntent.action = ACTION_RESET.text
        application.startService(serviceIntent)

    }

    fun saveServiceStatus() = saveData.saveBoolean(Constants.IS_SERVICE_RUNNING.text, isServiceRunning)

    fun getServiceStatus(): Boolean = saveData.getSavedBoolean(Constants.IS_SERVICE_RUNNING.text, false)






}
