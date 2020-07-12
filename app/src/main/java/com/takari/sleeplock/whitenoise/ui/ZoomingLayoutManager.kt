package com.takari.sleeplock.whitenoise.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * Zooms back and forth as the user scrolls horizontally for some nice visual flair.
 *
 * Props to this library for the inspiration: https://github.com/Spikeysanju/ZoomRecylerLayout
 */
class ZoomingLayoutManager(context: Context) : LinearLayoutManager(context) {

    private val shrinkAmount = 0.15f
    private val shrinkDistance = 0.9f
    private var isScrollingEnabled = true

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val orientation = orientation
        if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)

            val midpoint = width / 2f
            val d0 = 0f
            val d1 = shrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - shrinkAmount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childMidpoint = (getDecoratedRight(child!!) + getDecoratedLeft(child)) / 2f
                val d = d1.coerceAtMost(abs(midpoint - childMidpoint))
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                child.scaleX = scale
                child.scaleY = scale
            }
            return scrolled
        } else {
            return 0
        }
    }

    override fun canScrollHorizontally(): Boolean = isScrollingEnabled

    fun setScrollingEnabled(boolean: Boolean) {
        isScrollingEnabled = boolean
    }
}