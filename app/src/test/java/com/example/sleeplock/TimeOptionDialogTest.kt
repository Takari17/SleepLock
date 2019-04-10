package com.example.sleeplock

import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class TimeOptionDialogTest {

    lateinit var dialog: TimeOptionDialog


    @Before
    fun setUp() {
        dialog = TimeOptionDialog()
    }

    @Test
    fun onCreateDialog() {
    }

    @Test
    fun extractIntValuesTest(){
        val value = extractIntValues("20 Minutes is left")
        assertEquals(20, value)
    }

    // private method from dialog
    private fun extractIntValues(text: String): Int {
        // Separates the "Min" value from our array and returns only the int value
        return Integer.valueOf(text.replace("[^0-9]".toRegex(), ""))
    }


}