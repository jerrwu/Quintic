package com.jerrwu.quintic.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jerrwu.quintic.*
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.helpers.InfoHelper
import com.jerrwu.quintic.main.fragment.FragmentCal
import com.jerrwu.quintic.main.fragment.FragmentEntries
import com.jerrwu.quintic.main.fragment.FragmentSearch
import com.jerrwu.quintic.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val fragment1: Fragment =
        FragmentEntries()
    private val fragment2: Fragment =
        FragmentSearch()
    private val fragment3: Fragment =
        FragmentCal()
    private val navSheetFragment = NavSheetFragment()
    private val fm = supportFragmentManager
    var active = fragment1

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.menu_home -> {
                    toolbar_title.text = getText(R.string.app_title)
                    fm.beginTransaction()
                        .hide(active)
                        .show(fragment1)
                        .commit()
                    active = fragment1
                    fab.show()
                    searchButton.visibility = View.GONE
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_search -> {
                    toolbar_title.text = getText(R.string.menu_search)
                    fm.beginTransaction()
                        .hide(active)
                        .show(fragment2)
                        .commit()
                    active = fragment2
                    searchButton.visibility = View.VISIBLE
                    fab.hide()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_info -> {
                    toolbar_title.text = getText(R.string.menu_calendar)
                    fm.beginTransaction()
                        .hide(active)
                        .show(fragment3)
                        .commit()
                    active = fragment3
                    searchButton.visibility = View.GONE
                    fab.hide()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun showBottomSheetDialogFragment() {
        navSheetFragment.show(supportFragmentManager, navSheetFragment.tag)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setLayoutNavScrollBehaviour(sharedPreferences)
        setLayoutToolbarScrollBehaviour(sharedPreferences)
        mainLayout.invalidate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val darkToggle = sharedPreferences.getString("dark_toggle", "2")?.toInt()

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when (darkToggle) {
            -2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                if (!InfoHelper.isUsingNightMode(resources.configuration)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
            -1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setLayoutNavScrollBehaviour(sharedPreferences)
        setLayoutToolbarScrollBehaviour(sharedPreferences)

        for (fragment in fm.fragments) {
            fm.beginTransaction().remove(fragment).commit()
        }

        fm.beginTransaction().add(R.id.frag_container, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.frag_container, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.frag_container, fragment1, "1").commit()

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_navigation.selectedItemId = R.id.menu_home

        accountButton.setOnClickListener {
            showBottomSheetDialogFragment()
        }

        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        fab.setOnClickListener {
            val intent = Intent(this, EntryActivity::class.java)
            startActivity(intent)
        }

        PreferenceManager
            .setDefaultValues(this, R.xml.preferences, false)
    }

    private fun setLayoutNavScrollBehaviour(sharedPreferences: SharedPreferences) {
        val bottomNavToggle = sharedPreferences.getBoolean("bottomNavHide",false)
        if (bottomNavToggle) {
            enableLayoutNavScrollBehaviour()
        } else {
            disableLayoutNavScrollBehaviour()
        }
    }

    private fun enableLayoutNavScrollBehaviour() {
        val param1: CoordinatorLayout.LayoutParams = bottom_navigation.layoutParams as CoordinatorLayout.LayoutParams
        param1.behavior = HideBottomViewOnScrollBehavior<CoordinatorLayout>()
        val param2: CoordinatorLayout.LayoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        param2.behavior = HideBottomViewOnScrollBehavior<CoordinatorLayout>()
        mainPaddingBottom.visibility = View.GONE
    }

    private fun disableLayoutNavScrollBehaviour() {
        val param1: CoordinatorLayout.LayoutParams = bottom_navigation.layoutParams as CoordinatorLayout.LayoutParams
        param1.behavior = null
        val param2: CoordinatorLayout.LayoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        param2.behavior = null
        mainPaddingBottom.visibility = View.VISIBLE
    }

    private fun setLayoutToolbarScrollBehaviour(sharedPreferences: SharedPreferences) {
        val appBarToggle = sharedPreferences.getBoolean("appBarHide",false)
        if (appBarToggle) {
            enableLayoutToolbarScrollBehaviour()
        } else {
            disableLayoutToolbarScrollBehaviour()
        }
    }

    private fun enableLayoutToolbarScrollBehaviour() {
        val param1: AppBarLayout.LayoutParams = toolbar_top.layoutParams as AppBarLayout.LayoutParams
        param1.scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        mainPaddingTop.visibility = View.GONE
    }

    private fun disableLayoutToolbarScrollBehaviour() {
        val param1: AppBarLayout.LayoutParams = toolbar_top.layoutParams as AppBarLayout.LayoutParams
        param1.scrollFlags = 0
        mainPaddingTop.visibility = View.VISIBLE
    }
}

