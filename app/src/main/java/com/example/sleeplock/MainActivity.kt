package com.example.sleeplock

import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

// Todo: I should at least add tabs if Im goin the no action bar route
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = window.decorView

        val uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN

        view.systemUiVisibility = uiOption
        setContentView(R.layout.activity_main)


        val mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> MainFragment()
                1 -> ListFragment()
                else -> throw Resources.NotFoundException()
            }
        }

        override fun getCount(): Int = 2
    }
}
