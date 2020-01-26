package com.takari.sleeplock.feature.sleeptimer.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.jakewharton.rxrelay2.PublishRelay
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.common.logD
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy


class QuestionDialog : DialogFragment() {

    private val onClick = PublishRelay.create<Unit>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =

        AlertDialog.Builder(activity).apply {
            setTitle("How it works")

            setPositiveButton("Got it!") { _, _ ->
                onClick.accept(Unit)
            }

            setMessage("Once the timer goes off, the volume will slowly fade and the screen will turn off. Perfect for dozing off to a video!")
            setIcon(R.drawable.question_mark)

        }.create()

    fun getOnClick(): Observable<Unit> = onClick
}
