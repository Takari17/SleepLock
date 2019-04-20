package com.example.sleeplock.view

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.sleeplock.R
import com.example.sleeplock.view.fragments.ListFragment
import com.example.sleeplock.view.fragments.MainFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

/*
Lets see if we can get the timer working in out activity, if so we can pass the timer from the activity to our fragment

todo Once you fix this problem defnalty write a brief summaary of it just incase of someone ask us in an interview what was our hrdest bug
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = sectionsPagerAdapter

        val viewPager = findViewById<ViewPager>(R.id.container)

        viewPager.adapter = sectionsPagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tabs)

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
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
