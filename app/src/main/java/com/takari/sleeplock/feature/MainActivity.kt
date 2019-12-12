package com.takari.sleeplock.feature

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.takari.sleeplock.R
import com.takari.sleeplock.feature.sleeptimer.ui.SleepTimerFragment
import com.takari.sleeplock.feature.whitenoise.ui.WhiteNoiseFragment
import kotlinx.android.synthetic.main.activity_main.*

var isAppInBackground = false

class MainActivity : AppCompatActivity() {

    //don't wanna keep creating instances of these
    private val whiteNoiseFragment = WhiteNoiseFragment()
    private val sleepTimerFragment = SleepTimerFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.apply {
            setOnNavigationItemSelectedListener(navListener)
            setBackgroundColor(Color.parseColor("#2A2D31"))
        }

        window.navigationBarColor = Color.parseColor("#000000")

        replaceContainer(whiteNoiseFragment)
    }

    override fun onStart() {
        super.onStart()
        isAppInBackground = false
    }

    override fun onStop() {
        super.onStop()
        isAppInBackground = true
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.whiteNoise -> replaceContainer(whiteNoiseFragment)
            R.id.sleepTimer -> replaceContainer(sleepTimerFragment)
        }
        true
    }

    private fun replaceContainer(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)
    }
}
