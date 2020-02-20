package com.jerrwu.quintic.settings


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import com.jerrwu.quintic.BuildConfig
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.PreferenceKeys
import com.jerrwu.quintic.utils.StringUtils


class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        val TAG = SettingsFragment::class.java.simpleName
    }
    
    private lateinit var mSummaryList: Array<out String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        val darkToggle = sharedPreferences.getString(PreferenceKeys.PREFERENCE_DARK_MODE, "2")?.toInt()
        val darkPreference: ListPreference? = findPreference(PreferenceKeys.PREFERENCE_DARK_MODE)
        val versionPreference: Preference? = findPreference(PreferenceKeys.PREFERENCE_VERSION)
        val buildPreference: Preference? = findPreference(PreferenceKeys.PREFERENCE_BUILD)
        val debugPrefs: PreferenceCategory? = findPreference(PreferenceKeys.SECTION_DEBUG)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onPreferenceChangeListener)

        if (!BuildConfig.DEBUG) {
            preferenceScreen.removePreference(debugPrefs)
        }

        mSummaryList = StringUtils.getStringArray(R.array.dark_mode, activity)

        if (darkPreference != null) {
            when (darkToggle) {
                -1 -> darkPreference.summary = mSummaryList[0]
                0 -> darkPreference.summary = mSummaryList[1]
                1 -> darkPreference.summary = mSummaryList[2]
                2 -> darkPreference.summary = mSummaryList[3]
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
            when (key) {
                PreferenceKeys.PREFERENCE_DARK_MODE -> {
                    val darkPreference = findPreference(key) as ListPreference?
                    val darkToggle = sharedPreferences.getString(key, "2")?.toInt()
                    when (darkToggle) {
                        -1 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            darkPreference!!.summary = mSummaryList[0]
                        }
                        0 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                            darkPreference!!.summary = mSummaryList[1]
                        }
                        1 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            darkPreference!!.summary = mSummaryList[2]
                        }
                        2 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            darkPreference!!.summary = mSummaryList[3]
                        }
                    }
                }
                PreferenceKeys.PREFERENCE_HIDE_BOTTOM_NAV -> {
                    val intent = Intent("TOGGLE_DIALOG_KEY")
                    activity?.sendBroadcast(intent)
                }
                PreferenceKeys.PREFERENCE_HIDE_APP_BAR -> {
                    val intent = Intent("TOGGLE_DIALOG_KEY")
                    activity?.sendBroadcast(intent)
                }
            }
        }
}
