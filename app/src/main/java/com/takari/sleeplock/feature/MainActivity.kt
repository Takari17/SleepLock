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


class MainActivity : AppCompatActivity() {

    private val whiteNoiseFragment: Fragment =
        supportFragmentManager.findFragmentById(R.id.whiteNoiseFragment)
            ?: WhiteNoiseFragment()

    private val sleepTimerFragment: Fragment =
        supportFragmentManager.findFragmentById(R.id.sleepTimerFragment)
            ?: SleepTimerFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.apply {
            setOnNavigationItemSelectedListener(navListener)
            setBackgroundColor(Color.parseColor("#2A2D31"))
        }

        window.navigationBarColor = Color.parseColor("#000000")


        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, whiteNoiseFragment)
                .commit()
        }
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
            R.id.whiteNoise ->
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, whiteNoiseFragment)
                    .commit()

            R.id.sleepTimer ->
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.container, sleepTimerFragment)
                    .commit()
        }
        true
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)

        fun getIsAppInBackground(): Boolean = isAppInBackground
    }
}

private var isAppInBackground = false