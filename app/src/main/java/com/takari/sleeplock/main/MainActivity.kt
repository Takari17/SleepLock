package com.takari.sleeplock.main

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.takari.sleeplock.R
import com.takari.sleeplock.sleeptimer.ui.SleepTimerFragment
import com.takari.sleeplock.whitenoise.ui.WhiteNoiseFragment


class MainActivity : AppCompatActivity() {

    companion object {
        const val WHITE_NOISE_FRAGMENT = "noise"
        const val SLEEP_TIMER_FRAGMENT = "sleep"
    }

    private val viewModel by viewModels<MainViewModel>()

    private val mainFragment: Fragment by lazy {
        supportFragmentManager.findFragmentById(R.id.mainFragment) ?: MainFragment()
    }

    private val whiteNoiseFragment: Fragment by lazy {
        supportFragmentManager.findFragmentById(R.id.whiteNoiseFragment) ?: WhiteNoiseFragment()
    }

    private val sleepTimerFragment: Fragment by lazy {
        supportFragmentManager.findFragmentById(R.id.sleepTimerFragment) ?: SleepTimerFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setNavBarTransparentAndOverlappable()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, mainFragment)
                .commit()
        }

        viewModel.switchContainers = { fragmentName ->
            when (fragmentName) {
                FragmentName.WhiteNoiseFragment -> fadeInNewFragment(whiteNoiseFragment, WHITE_NOISE_FRAGMENT)
                FragmentName.SleepTimerFragment -> fadeInNewFragment(sleepTimerFragment, SLEEP_TIMER_FRAGMENT)
            }
        }
    }

    private fun fadeInNewFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(tag)
            .setCustomAnimations(R.anim.f_in, R.anim.f_out, R.anim.f_in, R.anim.f_out)
            .replace(R.id.container, fragment)
            .commit()
    }

    //Overlappable is now officially a word
    private fun setNavBarTransparentAndOverlappable() {
        window.navigationBarColor = Color.TRANSPARENT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}
