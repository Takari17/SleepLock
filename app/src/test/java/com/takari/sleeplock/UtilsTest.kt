package com.takari.sleeplock

import com.takari.sleeplock.shared.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class UtilsTest {

    @Test
    fun `hours should convert to min`() {
        val twoHr = 2
        val min = twoHr.hrToMin()
        assertEquals(120, min)
    }

    @Test
    fun `minutes should convert to milliseconds`() {
        val minutes = 10
        val milli = minutes.minToMilli()
        assertEquals(600000, milli)
    }

    @Test
    fun `milliseconds should convert to seconds`() {
        val milli = 15000L
        val seconds = milli.milliToSeconds()
        assertEquals(15, seconds)
    }

    @Test
    fun `should only be int values`() {
        val string = "1 stuff 2 +++ 3 =@# 4 boop 5"
        val onlyInts: Int = string.extractInts()
        assertEquals(12345, onlyInts)
    }

    @Test
    fun `should convert to 24hr format`() {
        val eightyMinInMilli = 4800000L
        val formattedTime = eightyMinInMilli.to24HourFormat()
        assertEquals("01:20:00", formattedTime)
    }
}
