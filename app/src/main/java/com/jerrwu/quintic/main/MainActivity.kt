package com.jerrwu.quintic.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jerrwu.quintic.BuildConfig
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.base.BaseActivity
import com.jerrwu.quintic.common.base.BaseFragment
import com.jerrwu.quintic.common.constants.Constants
import com.jerrwu.quintic.common.constants.PreferenceKeys
import com.jerrwu.quintic.entities.entry.adapter.EntryAdapter
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.main.fragment.CalFragment
import com.jerrwu.quintic.main.fragment.EntriesFragment
import com.jerrwu.quintic.main.fragment.SearchFragment
import com.jerrwu.quintic.search.SearchActivity
import com.jerrwu.quintic.utils.StringUtils
import com.jerrwu.quintic.utils.UiUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    // TODO: add viewpager to improve fragment switching

    override lateinit var mViewModel: MainViewModel

    private val mNavSheetFragment = NavSheetFragment()
    private val mFragmentManager = supportFragmentManager
    private var mActiveFragment: BaseFragment = EntriesFragment()
    var mRefreshCalFragmentGrid = false

    override fun onBackPressed() {
        val currentFragment = mActiveFragment
        if (currentFragment !is EntriesFragment) {
            bottom_navigation.selectedItemId = R.id.menu_home
        } else if (currentFragment.mAdapter != null &&
            (currentFragment.mAdapter as EntryAdapter).mIsMultiSelect) {
            currentFragment.hideSelectionToolbar(false)
        } else {
            super.onBackPressed()
        }
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var state = false
            var idx = 0

            mViewModel.navigationFragments.observe(this, Observer { navigationFragments ->
                if (mActiveFragment is EntriesFragment) {
                    val entriesFragment: EntriesFragment = mActiveFragment as EntriesFragment
                    if (entriesFragment.mAdapter != null &&
                        (entriesFragment.mAdapter as EntryAdapter).mIsMultiSelect) {
                        entriesFragment.hideSelectionToolbar(false)
                    }
                }

                when (item.itemId) {
                    R.id.menu_home -> {
                        idx = 0
                        // check if same
                        if (mActiveFragment is EntriesFragment) return@Observer

                        toolbar_title.text = getText(R.string.app_title)
                        mFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_from_bottom, R.anim.fade_out)
                            .hide(mActiveFragment)
                            .show(navigationFragments[idx])
                            .commit()
                        mActiveFragment = navigationFragments[idx]
                        fab.show()
                        search_button.visibility = View.GONE
                        state = true
                    }

                    R.id.menu_search -> {
                        idx = 1
                        if (mActiveFragment is SearchFragment) return@Observer

                        toolbar_title.text = getText(R.string.menu_search)
                        mFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_from_bottom, R.anim.fade_out)
                            .hide(mActiveFragment)
                            .show(navigationFragments[idx])
                            .commit()
                        mActiveFragment = navigationFragments[idx]
                        search_button.visibility = View.VISIBLE
                        fab.hide()
                        state = true
                    }

                    R.id.menu_calendar -> {
                        idx = 2
                        if (mActiveFragment is CalFragment) return@Observer

                        toolbar_title.text = getText(R.string.menu_calendar)
                        mFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_from_bottom, R.anim.fade_out)
                            .hide(mActiveFragment)
                            .show(navigationFragments[idx])
                            .commit()
                        mActiveFragment = navigationFragments[idx]
                        search_button.visibility = View.GONE
                        fab.show()
                        if (mRefreshCalFragmentGrid) {
                            mActiveFragment.onFragmentShown()
                            mRefreshCalFragmentGrid = false
                        }
                        state = true
                    }
                }

            })
            mViewModel.setActiveId(item.itemId)
            mViewModel.setActiveFragmentIndex(idx)
            state
        }

    private fun showBottomSheetDialogFragment() {
        mNavSheetFragment.show(supportFragmentManager, mNavSheetFragment.tag)
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getPreferences(this).observe(this, Observer { sharedPreferences ->
            setLayoutNavScrollBehaviour(sharedPreferences)
            setLayoutToolbarScrollBehaviour(sharedPreferences)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mViewModel.getPreferences(this).observe(this, Observer { sharedPreferences ->
            showChangelogIfUpdated(sharedPreferences)

            setLayoutNavScrollBehaviour(sharedPreferences)
            setLayoutToolbarScrollBehaviour(sharedPreferences)
        })

        for (fragment in mFragmentManager.fragments) {
            mFragmentManager.beginTransaction().remove(fragment).commit()
        }

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mViewModel.navigationFragments.observe(this, Observer { navigationFragments ->
            for (fragment in navigationFragments) {
                mFragmentManager
                    .beginTransaction()
                    .add(R.id.frag_container, fragment, fragment.tag)
                    .hide(fragment)
                    .commit()
            }
            mViewModel.activeFragmentIndex.observe(this, Observer { activeIndex ->
                mActiveFragment = navigationFragments[activeIndex]
                mFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .show(mActiveFragment)
                    .commit()
            })
        })

        mViewModel.activeId.observe(this, Observer { activeId ->
            bottom_navigation.selectedItemId = activeId
        })

        account_button.setOnClickListener {
            showBottomSheetDialogFragment()
        }

        search_button.setOnClickListener {
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

    private fun showChangelogIfUpdated(sharedPreferences: SharedPreferences) {
        if (sharedPreferences.contains(PreferenceKeys.PREFERENCE_PREVIOUS_VERSION)) {
            val previousVer = sharedPreferences.getString(PreferenceKeys.PREFERENCE_PREVIOUS_VERSION, "")
            if (previousVer != BuildConfig.VERSION_NAME) {
                UiUtils.showDismissOnlyDialog(
                    StringUtils.getString(R.string.changelog, this),
                    "${StringUtils.getString(R.string.updated_to_version, this)} ${BuildConfig.VERSION_NAME}." +
                            " \n\n\n ${Constants.CHANGELOG}",
                    StringUtils.getString(R.string.ok, this),
                    this)
            }
        }
        sharedPreferences.edit().putString(PreferenceKeys.PREFERENCE_PREVIOUS_VERSION, BuildConfig.VERSION_NAME).apply()
    }

    private fun setLayoutNavScrollBehaviour(sharedPreferences: SharedPreferences) {
        val bottomNavToggle = sharedPreferences.getBoolean(PreferenceKeys.PREFERENCE_HIDE_BOTTOM_NAV,false)
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
        main_padding_bottom.visibility = View.GONE
    }

    private fun disableLayoutNavScrollBehaviour() {
        val param1: CoordinatorLayout.LayoutParams = bottom_navigation.layoutParams as CoordinatorLayout.LayoutParams
        param1.behavior = null
        val param2: CoordinatorLayout.LayoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        param2.behavior = null
        main_padding_bottom.visibility = View.VISIBLE
    }

    private fun setLayoutToolbarScrollBehaviour(sharedPreferences: SharedPreferences) {
        val appBarToggle = sharedPreferences.getBoolean(PreferenceKeys.PREFERENCE_HIDE_APP_BAR,false)
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
        main_padding_top.visibility = View.GONE
    }

    private fun disableLayoutToolbarScrollBehaviour() {
        val param1: AppBarLayout.LayoutParams = toolbar_top.layoutParams as AppBarLayout.LayoutParams
        param1.scrollFlags = 0
        main_padding_top.visibility = View.VISIBLE
    }
}

