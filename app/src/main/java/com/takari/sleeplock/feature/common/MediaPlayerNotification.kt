package com.takari.sleeplock.feature.common

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.takari.sleeplock.App.Companion.CHANNEL_ID
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.MainActivity


class MediaPlayerNotification(
    val id: Int,
    private val context: Context,
    private val resumeIntent: PendingIntent,
    private val pauseIntent: PendingIntent,
    private val resetIntent: PendingIntent
) {

    fun notification(newText: String, resumeOrPause: ResumeOrPause): Notification =
        NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.alarm_icon)
            addResumeOrPauseAction(resumeOrPause)
            addAction(R.drawable.reset, "Reset", resetIntent)
            setStyle(MediaStyle().setShowActionsInCompactView(0, 1))
            setSubText("Sound Options")
            setContentTitle("SleepLock")
            setContentText(newText)
            setContentIntent(mainActivityIntent())
        }.build()


    fun update(newText: String) {
        NotificationManagerCompat.from(context)
            .notify(id, notification(newText, ResumeOrPause.Resume))
    }

    private fun mainActivityIntent() = PendingIntent.getActivity(
        context,
        1,
        MainActivity.createIntent(context),
        0
    )

    //used to alternate between the pause and resume buttons on the notification
    private fun NotificationCompat.Builder.addResumeOrPauseAction(resumeOrPause: ResumeOrPause): NotificationCompat.Builder =
        if (resumeOrPause == ResumeOrPause.Resume)
            this.addAction(R.drawable.pause, "Resume", pauseIntent)
        else
            this.addAction(R.drawable.play, "Pause", resumeIntent)


    enum class ResumeOrPause {
        Resume, Pause
    }
}
