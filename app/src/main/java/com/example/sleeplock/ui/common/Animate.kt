package com.example.sleeplock.ui.common

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.view.View
import androidx.core.animation.doOnStart

/*
 * Handles the animation in the MainFragment when the user clicks on the startPauseButton.
 */
class Animate {

    // Durations
    companion object {
        const val DEFAULT: Long = 500
        const val INSTANT: Long = 0
    }

    private fun translationLeft(view: View, durationMillis: Long = 500): ValueAnimator =
        ValueAnimator.ofFloat(0f, -170f).apply {
            duration = durationMillis

            addUpdateListener { animation -> view.translationX = animation.animatedValue as Float }
        }

    private fun translationRight(view: View, durationMillis: Long = 500): ValueAnimator =
        ValueAnimator.ofFloat(0f, 170f).apply {
            duration = durationMillis

            addUpdateListener { animation -> view.translationX = animation.animatedValue as Float }
        }

    private fun translationDown(view: View, durationMillis: Long = 500): ValueAnimator =
        ValueAnimator.ofFloat(0f, 112.5f).apply {
            duration = durationMillis

            addUpdateListener { animation -> view.translationY = animation.animatedValue as Float }
        }

    private fun fadeOut(view: View, durationMillis: Long = 500): ValueAnimator =
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = durationMillis

            addUpdateListener { animation -> view.alpha = animation.animatedValue as Float }

            doOnStart { view.isClickable = false }
        }

    private fun fadeIn(view: View, durationMillis: Long = 500): ValueAnimator {
        view.visibility = View.VISIBLE

        return ValueAnimator.ofFloat(0f, 1f).apply {
            duration = durationMillis

            addUpdateListener { animation -> view.alpha = animation.animatedValue as Float }

            doOnStart { view.isClickable = true }
        }
    }


    fun translateAll(startButton: View, resetButton: View, fab: View, durationMillis: Long = 500) =

        AnimatorSet().apply {
            playTogether(
                translationLeft(startButton, durationMillis),
                translationDown(resetButton, durationMillis),
                translationRight(resetButton, durationMillis),
                fadeIn(resetButton, durationMillis),
                fadeOut(fab, durationMillis)
            )
        }.start()

    @TargetApi(Build.VERSION_CODES.O)
    fun reverseTranslateAll(startButton: View, resetButton: View, fab: View) =

        AnimatorSet().apply {
            playTogether(
                translationLeft(startButton),
                translationDown(resetButton),
                translationRight(resetButton),
                fadeOut(resetButton),
                fadeIn(fab)
            )
        }.reverse()


    fun fadeInAll(resetButton: View, fab: View, durationMillis: Long = 500) =
        AnimatorSet().apply {
            playTogether(
                fadeIn(resetButton, durationMillis),
                fadeOut(fab, durationMillis)
            )
        }.start()


    fun fadeOutAll(resetButton: View, fab: View) =
        AnimatorSet().apply {
            playTogether(
                fadeOut(resetButton),
                fadeIn(fab)
            )
        }.start()
}