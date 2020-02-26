package com.jerrwu.quintic.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.jerrwu.quintic.R
import com.jerrwu.quintic.account.AccountActivity
import com.jerrwu.quintic.common.BaseBottomSheetFragment
import com.jerrwu.quintic.common.constants.PreferenceKeys
import com.jerrwu.quintic.utils.StringUtils
import com.jerrwu.quintic.settings.SettingsActivity
import kotlinx.android.synthetic.main.nav_sheet.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NavSheetFragment : BaseBottomSheetFragment() {
    companion object {
        val TAG = NavSheetFragment::class.java.simpleName
    }

    private var mGreetingString = ""
    override val mLayoutRes: Int = R.layout.nav_sheet
    override val mLayoutId: Int = R.id.nav_sheet_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        val nameString = prefs.getString(PreferenceKeys.PREFERENCE_NAME, "User")
        bottom_sheet_account_text_1.text = nameString

        val greetingsToggle = prefs.getBoolean(PreferenceKeys.PREFERENCE_GREETINGS, true)
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

        settings_button.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
            closeSheet()
        }

        doc_button.setOnClickListener {
            val uri = Uri.parse(getString(R.string.github_link_readme))
            val intentBuilder = CustomTabsIntent.Builder()
            intentBuilder.setToolbarColor(
                context.let { ContextCompat.getColor(it!!,
                    R.color.colorPrimary
                ) })
            intentBuilder.setShowTitle(true)
            val customTabsIntent = intentBuilder.build()
            activity?.let { it1 -> customTabsIntent.launchUrl(it1, uri) }
            closeSheet()
        }

        help_button.setOnClickListener {
            val uri = Uri.parse(getString(R.string.github_link_issues))
            val intentBuilder = CustomTabsIntent.Builder()
            intentBuilder.setToolbarColor(
                context.let { ContextCompat.getColor(it!!,
                    R.color.colorPrimary
                ) })
            intentBuilder.setShowTitle(true)
            val customTabsIntent = intentBuilder.build()
            activity?.let { it1 -> customTabsIntent.launchUrl(it1, uri) }
            closeSheet()
        }

        bottom_sheet_account_button.setOnClickListener {
            val intent = Intent(activity, AccountActivity::class.java)
            startActivity(intent)
            closeSheet()
        }
    }
}