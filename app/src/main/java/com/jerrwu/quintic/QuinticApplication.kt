package com.jerrwu.quintic

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.jerrwu.quintic.common.constants.PreferenceKeys

class QuinticApplication : Application() {
    companion object {
        val TAG = QuinticApplication::class.java.simpleName
        var ctx: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        initTheme()
        ctx = applicationContext
    }

    private fun initTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        when (sharedPreferences.getString(PreferenceKeys.PREFERENCE_DARK_MODE, "-1")?.toInt()) {
            -1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}