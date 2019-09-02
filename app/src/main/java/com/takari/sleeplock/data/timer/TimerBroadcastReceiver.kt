package com.takari.sleeplock.data.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.takari.sleeplock.data.SleepTimerService

/**
 * Receives click events from SleepTimerService's notification actions and invokes the
 * services onStartCommand with an action specified intent.
 */
class TimerBroadcastReceiver : BroadcastReceiver() {

    companion object {
        fun createIntent(context: Context, action: String) =
            Intent(context, TimerBroadcastReceiver::class.java).apply {
                this.action = action
            }
    }

    override fun onReceive(context: Context, intent: Intent) {

        Intent(context, SleepTimerService::class.java).also { serviceIntent ->
            serviceIntent.action = intent.action
            context.startService(serviceIntent)
        }
    }
}