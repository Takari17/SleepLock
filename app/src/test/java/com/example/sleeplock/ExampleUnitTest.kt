package com.example.sleeplock

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private lateinit var task: Task

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Before
    fun setUp(){
        task = Task("Fuck some traps")
    }

    @Test
    fun didTaskComplete(){
        assertEquals("Did Fuck some traps", task.doTask(task.job))
    }



}


class Task(val job: String){

    fun doTask(job: String): String{
        return "Did $job"
    }

}