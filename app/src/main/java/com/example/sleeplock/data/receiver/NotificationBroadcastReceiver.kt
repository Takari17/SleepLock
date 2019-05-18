package com.example.sleeplock.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sleeplock.data.service.MyService
import com.example.sleeplock.utils.ACTION_PAUSE
import com.example.sleeplock.utils.ACTION_PLAY
import com.example.sleeplock.utils.ACTION_RESET

// Receives click events from the foreground notification button
class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val serviceIntent = Intent(context, MyService::class.java)

        serviceIntent.action = intent.action

        context.startService(serviceIntent)
    }
}