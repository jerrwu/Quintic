package com.jerrwu.quintic

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.nav_sheet.*
import android.net.Uri
import android.preference.PreferenceManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NavSheetFragment : BottomSheetDialogFragment() {
    private var fragmentView: View? = null
    private var greetingString = ""


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view!!.parent.parent.parent as View).fitsSystemWindows = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.nav_sheet, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        if (InfoHelper.hasNavBar(activity)) {
            nav_sheet_layout.setPadding(0,0,0,128)
        }

        val nameString = prefs.getString("name", "User")
        bottom_sheet_account_text_1.text = nameString

        val greetingsToggle = prefs.getBoolean("greetings", true)
        if (greetingsToggle) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH")
            val curHour: String =  current.format(formatter)
            greetingString = InfoHelper.getGreeting(curHour)
            bottom_sheet_account_text_2.visibility = View.VISIBLE
        } else {
            greetingString = ""
            bottom_sheet_account_text_2.visibility = View.GONE
        }

        bottom_sheet_account_text_2.text = greetingString

        settingsButton.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
            closeNavSheet()
        }

        docButton.setOnClickListener {
            val uri = Uri.parse(getString(R.string.github_link_readme))
            val intentBuilder = CustomTabsIntent.Builder()
            intentBuilder.setToolbarColor(
                context.let { ContextCompat.getColor(it!!, R.color.colorPrimary) })
            intentBuilder.setShowTitle(true)
            val customTabsIntent = intentBuilder.build()
            activity?.let { it1 -> customTabsIntent.launchUrl(it1, uri) }
            closeNavSheet()
        }

        helpButton.setOnClickListener {
            val uri = Uri.parse(getString(R.string.github_link_issues))
            val intentBuilder = CustomTabsIntent.Builder()
            intentBuilder.setToolbarColor(
                context.let { ContextCompat.getColor(it!!, R.color.colorPrimary) })
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