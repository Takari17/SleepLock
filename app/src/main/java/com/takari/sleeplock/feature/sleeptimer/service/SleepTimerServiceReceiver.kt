package com.takari.sleeplock.feature.sleeptimer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class SleepTimerServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Intent(context, SleepTimerService::class.java).apply {
            action = intent.action
            context.startService(this)
        }
    }

    companion object {
        fun createIntent(context: Context, action: String) =
            Intent(context, SleepTimerServiceReceiver::class.java).apply {
                this.action = action
            }
    }
}
