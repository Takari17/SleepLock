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
        val action = intent.action
        val actionIntent = Intent(context, MyService::class.java)

        when (action) {
            ACTION_PLAY -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }

            ACTION_PAUSE -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }

            ACTION_RESET -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }
        }
    }
}