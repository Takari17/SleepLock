package com.example.sleeplock

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class HelloWorldEspressoTest {



    @Test
    fun testFab() {
        onView(withId(R.id.fab))
            .perform(click())

    }


    @Test
    fun testRecyclerView() {

        onView(withId(R.id.fab)).perform(click())


    }
}
