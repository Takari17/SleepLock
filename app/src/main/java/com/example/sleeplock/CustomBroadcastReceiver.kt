package com.example.sleeplock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.example.sleeplock.Constants.ACTION_PLAY
import com.example.sleeplock.Constants.ACTION_PAUSE
import com.example.sleeplock.Constants.ACTION_RESET

/*
When an notification action is clicked, the broadcast receiver sets an action for the intent,
and invokes the start on command method in our service, which gets filtered in a when statement
 */
// todo just add this below your service instead of a separate file
class CustomBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val actionIntent = Intent(context, CustomService::class.java)

        when (action) {
            ACTION_PLAY.text -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }

            ACTION_PAUSE.text -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }

            ACTION_RESET.text -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }
        }
    }


}