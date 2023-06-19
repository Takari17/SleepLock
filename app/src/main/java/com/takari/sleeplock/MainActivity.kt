package com.takari.sleeplock

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.takari.sleeplock.main.HomeScreenFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {

        fun fadeInFragment(tag: String, fragmentManager: FragmentManager, fragment: Fragment) {
            fragmentManager
                .beginTransaction()
                .addToBackStack(tag)
                .setCustomAnimations(
                    R.anim.fade_in, R.anim.fade_out,
                    R.anim.fade_in, R.anim.fade_out
                )
                .replace(R.id.container, fragment)
                .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTopNavBarTransparent()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, HomeScreenFragment())
                .commit()
        }
    }

    private fun setTopNavBarTransparent() {
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}
