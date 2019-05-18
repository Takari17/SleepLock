package com.example.sleeplock.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sleeplock.data.service.MainService

// Receives click events from the foreground notification button
class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val serviceIntent = Intent(context, MainService::class.java)

        serviceIntent.action = intent.action

        context.startService(serviceIntent)
    }
}