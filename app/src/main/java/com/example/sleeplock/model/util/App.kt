package com.example.sleeplock.model.util

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.sleeplock.model.util.Constants.CHANNEL_ID

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val serviceChannel = NotificationChannel(
                CHANNEL_ID.text, "Sleep Lock",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(serviceChannel)
        }
    }

}
