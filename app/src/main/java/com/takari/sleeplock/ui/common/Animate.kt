package com.takari.sleeplock.ui.common

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.TypedValue
import android.view.View
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator

/*
 * Handles the animation in the TimerFragment when the user clicks on the startPauseButton.
 */
class Animate(
    private val context: Context
) {

    // Durations
    companion object {
        const val DEFAULT: Long = 600
        const val INSTANT: Long = 0
    }

    private fun translationLeft(view: View, duration: Long = 600): ObjectAnimator =
        view.customTranslateX(-70f, duration)

    private fun reverseTranslationLeft(view: View): ObjectAnimator =
        view.resetTranslateX()


    private fun translationRight(view: View, duration: Long = 600): ObjectAnimator =
        view.customTranslateX(75f, duration)

    private fun reverseTranslationRight(view: View): ObjectAnimator =
        view.resetTranslateX()


    private fun translationDown(view: View, duration: Long = 600): ObjectAnimator =
        view.customTranslateY(55.5f, duration)

    private fun reverseTranslationDown(view: View): ObjectAnimator =
        view.resetTranslateY()


    private fun fadeOut(view: View, duration: Long = 600): ObjectAnimator =
        view.customFadeAnimation(0f, duration)


    private fun fadeIn(view: View, duration: Long = 600): ObjectAnimator =
        view.customFadeAnimation(1f, duration).also {
            view.visibility = View.VISIBLE
        }


    fun translateAll(startButton: View, resetButton: View, fab: View, durationMillis: Long = 600) =
        animateTogether(
            translationLeft(startButton, durationMillis),
            translationDown(resetButton, durationMillis),
            translationRight(resetButton, durationMillis),
            fadeIn(resetButton, durationMillis),
            fadeOut(fab, durationMillis)
        ).start()


    /*
    The reverse() method on the Animator Set is only supported on APi 26+ so I just decided
    to create custom reverse animation methods for my translations instead.
     */
    fun reverseTranslateAll(startButton: View, resetButton: View, fab: View) =
        animateTogether(
            reverseTranslationLeft(startButton),
            reverseTranslationDown(resetButton),
            reverseTranslationRight(resetButton),
            fadeOut(resetButton),
            fadeIn(fab)
        ).start()


    fun fadeInAll(resetButton: View, fab: View, durationMillis: Long = 600) =
        animateTogether(
            fadeIn(resetButton, durationMillis),
            fadeOut(fab, durationMillis)
        ).start()


    fun fadeOutAll(resetButton: View, fab: View) =
        animateTogether(
            fadeOut(resetButton),
            fadeIn(fab)
        ).start()

    /*
    Since the animation units are pixels I converted them to dp so the animation
    scales accordingly with different devices.

    So proud of these extension functions <3
     */
    private fun <T : View> T.customTranslateX(pxDistance: Float, duration: Long): ObjectAnimator =
        ViewPropertyObjectAnimator.animate(this).apply {
            translationX(pxDistance.toDp())
            setDuration(duration)
        }.get()


    //Reset, as in return to your initial position
    private fun <T : View> T.resetTranslateX(): ObjectAnimator =
        ViewPropertyObjectAnimator.animate(this).apply {
            translationX(0f.toDp())
            setDuration(600)
        }.get()


    private fun <T : View> T.customTranslateY(pxDistance: Float, duration: Long): ObjectAnimator =
        ViewPropertyObjectAnimator.animate(this).apply {
            translationY(pxDistance.toDp())
            setDuration(duration)
        }.get()

    private fun <T : View> T.resetTranslateY(): ObjectAnimator =
        ViewPropertyObjectAnimator.animate(this).apply {
            translationY(0f.toDp())
            setDuration(600)
        }.get()


    private fun <T : View> T.customFadeAnimation(alpha: Float, duration: Long): ObjectAnimator =
        ViewPropertyObjectAnimator.animate(this).apply {
            alpha(alpha)
            setDuration(duration)
        }.get()


    //Converts floats to dp
    private fun Float.toDp(): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this,
            context.resources.displayMetrics
        )


    // Thanks Zhuinden!
    private fun animateTogether(vararg animators: Animator): AnimatorSet =
        AnimatorSet().apply {
            playTogether(*animators)
        }
}