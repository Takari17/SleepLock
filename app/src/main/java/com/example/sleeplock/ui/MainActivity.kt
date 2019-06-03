package com.example.sleeplock.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.sleeplock.R
import com.example.sleeplock.ui.fragments.ListFragment
import com.example.sleeplock.ui.fragments.MainFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

var isAppInBackground = false

class MainActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = sectionsPagerAdapter

        val viewPager = findViewById<ViewPager>(R.id.container).apply {
            adapter = sectionsPagerAdapter
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabs)

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment =

            when (position) {
                0 -> MainFragment()
                1 -> ListFragment()
                else -> throw Resources.NotFoundException()
            }

        override fun getCount(): Int = 2
    }

    override fun onStart() {
        super.onStart()
        isAppInBackground = false
    }

    override fun onStop() {
        super.onStop()
        isAppInBackground = true
    }
}