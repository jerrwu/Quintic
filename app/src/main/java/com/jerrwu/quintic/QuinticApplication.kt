package com.jerrwu.quintic

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class QuinticApplication : Application() {
    companion object {
        var ctx: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        initTheme()
        ctx = applicationContext
    }

    private fun initTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        when (sharedPreferences.getString("dark_toggle", "-1")?.toInt()) {
            -1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}