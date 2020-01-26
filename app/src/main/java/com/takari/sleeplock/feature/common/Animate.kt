package com.takari.sleeplock.feature.common

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.TypedValue
import android.view.View
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator


/*
Looking back I have no idea what I was thinking.....but it works ¯\_(ツ)_/¯
Refactor to constraint sets if you decide to do another major update.
 */
class Animate(private val context: Context) {

    fun start(
        startPauseButton: View,
        resetButton: View,
        fab: View,
        duration: Duration
    ) {
        animateTogether(
            translationLeft(startPauseButton, duration.length),
            translationDown(resetButton, duration.length),
            translationRight(resetButton, duration.length),
            fadeIn(resetButton, duration.length),
            fadeOut(fab, duration.length),
            fadeIn(resetButton, duration.length),
            fadeOut(fab, duration.length)
        ).start()

    }

    fun reset(
        startPauseButton: View,
        resetButton: View,
        fab: View
    ) {
        animateTogether(
            reverseTranslationLeft(startPauseButton),
            reverseTranslationDown(resetButton),
            reverseTranslationRight(resetButton),
            fadeOut(resetButton),
            fadeIn(fab),
            fadeOut(resetButton),
            fadeIn(fab)
        ).start()
    }


    private fun translationLeft(view: View, duration: Long = 600): ObjectAnimator =
        view.customTranslateX(-85f, duration)


    private fun translationRight(view: View, duration: Long = 600): ObjectAnimator =
        view.customTranslateX(85f, duration)


    private fun reverseTranslationLeft(view: View): ObjectAnimator =
        view.resetTranslateX()


    private fun reverseTranslationRight(view: View): ObjectAnimator =
        view.resetTranslateX()


    private fun translationDown(view: View, duration: Long = 600): ObjectAnimator =
        view.customTranslateY(49f, duration)


    private fun reverseTranslationDown(view: View): ObjectAnimator =
        view.resetTranslateY()


    private fun fadeOut(view: View, duration: Long = 600): ObjectAnimator =
        view.customFadeAnimation(0f, duration)


    private fun fadeIn(view: View, duration: Long = 600): ObjectAnimator =
        view.customFadeAnimation(1f, duration).also {
            view.visibility = View.VISIBLE
        }


    private fun <T : View> T.customTranslateX(pxDistance: Float, duration: Long): ObjectAnimator =
        ViewPropertyObjectAnimator.animate(this).apply {
            translationX(pxDistance.toDp())
            setDuration(duration)
        }.get()


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


    private fun Float.toDp(): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this,
            context.resources.displayMetrics
        )


    private fun animateTogether(vararg animators: Animator): AnimatorSet =
        AnimatorSet().apply {
            playTogether(*animators)
        }

    enum class Duration(val length: Long) {
        DEFAULT(600),
        INSTANT(0)
    }
}

