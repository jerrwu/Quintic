package com.jerrwu.quintic.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.jerrwu.quintic.R
import com.jerrwu.quintic.helpers.InfoHelper
import com.jerrwu.quintic.helpers.StringHelper


class SettingsActivity : AppCompatActivity() {
    val activity = this
    val key1 = "TOGGLE_DIALOG_KEY"

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            InfoHelper.showDialog(
                StringHelper.getString(R.string.confirm_restart_title, context),
                StringHelper.getString(R.string.confirm_restart, context),
                StringHelper.getString(R.string.restart_yes, context),
                StringHelper.getString(R.string.restart_no, context),
                activity, InfoHelper::restartApp, InfoHelper::dismissDialog)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mBroadcastReceiver, IntentFilter(key1))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsFragment = SettingsFragment()

        settingsBackButton.setOnClickListener {
            finish()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.settingsFrame, settingsFragment)
            .commit()
    }
}
