package com.jerrwu.quintic.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jerrwu.quintic.R
import com.jerrwu.quintic.account.AccountActivity
import com.jerrwu.quintic.utils.UiUtils
import com.jerrwu.quintic.utils.StringUtils
import com.jerrwu.quintic.settings.SettingsActivity
import kotlinx.android.synthetic.main.nav_sheet.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NavSheetFragment : BottomSheetDialogFragment() {
    companion object {
        val TAG = NavSheetFragment::class.java.simpleName
    }

    private var mFragmentView: View? = null
    private var mGreetingString = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view!!.parent.parent.parent as View).fitsSystemWindows = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentView = inflater.inflate(R.layout.nav_sheet, container, false)
        return mFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        if (UiUtils.hasNavBar(activity)) {
            nav_sheet_layout.setPadding(0,0,0,128)
        }

        val nameString = prefs.getString("name", "User")
        bottom_sheet_account_text_1.text = nameString

        val greetingsToggle = prefs.getBoolean("greetings", true)
        if (greetingsToggle) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH")
            val curHour: String =  current.format(formatter)
            mGreetingString = StringUtils.getGreeting(curHour, context)
            bottom_sheet_account_text_2.visibility = View.VISIBLE
        } else {
            mGreetingString = ""
            bottom_sheet_account_text_2.visibility = View.GONE
        }

        bottom_sheet_account_text_2.text = mGreetingString

        settingsButton.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
            closeNavSheet()
        }

        docButton.setOnClickListener {
            val uri = Uri.parse(getString(R.string.github_link_readme))
            val intentBuilder = CustomTabsIntent.Builder()
            intentBuilder.setToolbarColor(
                context.let { ContextCompat.getColor(it!!,
                    R.color.colorPrimary
                ) })
            intentBuilder.setShowTitle(true)
            val customTabsIntent = intentBuilder.build()
            activity?.let { it1 -> customTabsIntent.launchUrl(it1, uri) }
            closeNavSheet()
        }

        helpButton.setOnClickListener {
            val uri = Uri.parse(getString(R.string.github_link_issues))
            val intentBuilder = CustomTabsIntent.Builder()
            intentBuilder.setToolbarColor(
                context.let { ContextCompat.getColor(it!!,
                    R.color.colorPrimary
                ) })
            intentBuilder.setShowTitle(true)
            val customTabsIntent = intentBuilder.build()
            activity?.let { it1 -> customTabsIntent.launchUrl(it1, uri) }
            closeNavSheet()
        }

        bottomSheetAccountButton.setOnClickListener {
            val intent = Intent(activity, AccountActivity::class.java)
            startActivity(intent)
            closeNavSheet()
        }
    }

    private fun closeNavSheet() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }
}