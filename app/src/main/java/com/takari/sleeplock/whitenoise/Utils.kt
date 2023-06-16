package com.takari.sleeplock.whitenoise

fun Int.minToMilli(): Long = (this * 60000).toLong()

fun Int.hrToMilli(): Long = (this * 3.6e+6).toLong()
