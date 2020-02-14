package com.jerrwu.quintic.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseFragment
import com.jerrwu.quintic.entities.entry.adapter.EntryAdapter
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.main.fragment.FragmentCal
import com.jerrwu.quintic.main.fragment.FragmentEntries
import com.jerrwu.quintic.main.fragment.FragmentSearch
import com.jerrwu.quintic.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private val mEntriesFragment: BaseFragment =
        FragmentEntries()
    private val mSearchFragment: BaseFragment =
        FragmentSearch()
    private val mCalFragment: BaseFragment =
        FragmentCal()
    private val mNavSheetFragment = NavSheetFragment()
    private val mFragmentManager = supportFragmentManager
    private var mActiveFragment: BaseFragment = mEntriesFragment
    var mRefreshCalFragmentGrid = false

    override fun onBackPressed() {
        val currentFragment = mActiveFragment
        if (currentFragment !is FragmentEntries) {
            bottomNavigation.selectedItemId = R.id.menu_home
        } else if (currentFragment.mAdapter != null &&
            (currentFragment.mAdapter as EntryAdapter).mIsMultiSelect) {
            currentFragment.hideSelectionToolbar()
        } else {
            super.onBackPressed()
        }
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            if (mActiveFragment is FragmentEntries) {
                val fragmentEntries: FragmentEntries = mActiveFragment as FragmentEntries
                if (fragmentEntries.mAdapter != null &&
                    (fragmentEntries.mAdapter as EntryAdapter).mIsMultiSelect) {
                    fragmentEntries.hideSelectionToolbar()
                }
            }

            when (item.itemId) {
                R.id.menu_home -> {
                    // check if same
                    if (mActiveFragment is FragmentEntries) return@OnNavigationItemSelectedListener false

                    toolbar_title.text = getText(R.string.app_title)
                    mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_bottom, R.anim.fade_out)
                        .hide(mActiveFragment)
                        .show(mEntriesFragment)
                        .commit()
                    mActiveFragment = mEntriesFragment
                    fab.show()
                    searchButton.visibility = View.GONE
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_search -> {
                    if (mActiveFragment is FragmentSearch) return@OnNavigationItemSelectedListener false

                    toolbar_title.text = getText(R.string.menu_search)
                    mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_bottom, 0)
                        .hide(mActiveFragment)
                        .show(mSearchFragment)
                        .commit()
                    mActiveFragment = mSearchFragment
                    searchButton.visibility = View.VISIBLE
                    fab.hide()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_calendar -> {
                    if (mActiveFragment is FragmentCal) return@OnNavigationItemSelectedListener false

                    toolbar_title.text = getText(R.string.menu_calendar)
                    mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_bottom, R.anim.fade_out)
                        .hide(mActiveFragment)
                        .show(mCalFragment)
                        .commit()
                    mActiveFragment = mCalFragment
                    searchButton.visibility = View.GONE
                    fab.hide()
                    if (mRefreshCalFragmentGrid) {
                        mCalFragment.onFragmentShown()
                        mRefreshCalFragmentGrid = false
                    }
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun showBottomSheetDialogFragment() {
        mNavSheetFragment.show(supportFragmentManager, mNavSheetFragment.tag)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setLayoutNavScrollBehaviour(sharedPreferences)
        setLayoutToolbarScrollBehaviour(sharedPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setLayoutNavScrollBehaviour(sharedPreferences)
        setLayoutToolbarScrollBehaviour(sharedPreferences)

        for (fragment in mFragmentManager.fragments) {
            mFragmentManager.beginTransaction().remove(fragment).commit()
        }

        mFragmentManager.beginTransaction().add(R.id.frag_container, mCalFragment, "3").hide(mCalFragment).commit()
        mFragmentManager.beginTransaction().add(R.id.frag_container, mSearchFragment, "2").hide(mSearchFragment).commit()
        mFragmentManager.beginTransaction().add(R.id.frag_container, mEntriesFragment, "1").commit()

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomNavigation.selectedItemId = R.id.menu_home

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
        val param1: CoordinatorLayout.LayoutParams = bottomNavigation.layoutParams as CoordinatorLayout.LayoutParams
        param1.behavior = HideBottomViewOnScrollBehavior<CoordinatorLayout>()
        val param2: CoordinatorLayout.LayoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        param2.behavior = HideBottomViewOnScrollBehavior<CoordinatorLayout>()
        mainPaddingBottom.visibility = View.GONE
    }

    private fun disableLayoutNavScrollBehaviour() {
        val param1: CoordinatorLayout.LayoutParams = bottomNavigation.layoutParams as CoordinatorLayout.LayoutParams
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

