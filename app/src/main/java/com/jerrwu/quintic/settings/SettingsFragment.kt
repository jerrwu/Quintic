package com.jerrwu.quintic.settings


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import com.jerrwu.quintic.BuildConfig
import com.jerrwu.quintic.R


class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        val TAG = SettingsFragment::class.java.simpleName
    }

    private var mActivity: Activity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mActivity == null) {
            mActivity = this.activity!!
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        val darkToggle = sharedPreferences.getString("dark_toggle", "2")?.toInt()
        val darkPreference: ListPreference? = findPreference("dark_toggle")
        val versionPreference: Preference? = findPreference("version")
        val buildPreference: Preference? = findPreference("build")
        val debugPrefs: PreferenceCategory? = findPreference("debugPrefs")
        sharedPreferences.registerOnSharedPreferenceChangeListener(onPreferenceChangeListener)

        if (!BuildConfig.DEBUG) {
            preferenceScreen.removePreference(debugPrefs)
        }

        if (darkPreference != null) {
            when (darkToggle) {
                -1 -> darkPreference.summary = "Follow System"
                0 -> darkPreference.summary = "Set by Battery Saver"
                1 -> darkPreference.summary = "On"
                2 -> darkPreference.summary = "Off"
            }
        }

        if (versionPreference != null) {
            versionPreference.summary = BuildConfig.VERSION_NAME
        }

        if (buildPreference != null) {
            buildPreference.summary = BuildConfig.BUILD_NUMBER
        }
    }

    private var onPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "dark_toggle") {
                val darkPreference = findPreference(key) as ListPreference?
                val darkToggle = sharedPreferences.getString(key, "2")?.toInt()
                when (darkToggle) {
                    -1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        darkPreference!!.summary = "Follow System"
                    }
                    0 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                        darkPreference!!.summary = "Set by Battery Saver"
                    }
                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        darkPreference!!.summary = "On"
                    }
                    2 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        darkPreference!!.summary = "Off"
                    }
                }
            }
            else if (key == "bottomNavHide") {
                val intent = Intent("TOGGLE_DIALOG_KEY")
                mActivity!!.sendBroadcast(intent)
            }
            else if (key == "appBarHide") {
                val intent = Intent("TOGGLE_DIALOG_KEY")
                mActivity!!.sendBroadcast(intent)
            }
        }
}
