package com.jerrwu.quintic.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jerrwu.quintic.R
import com.jerrwu.quintic.utils.UiUtils
import com.jerrwu.quintic.utils.StringUtils
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    companion object {
        val TAG = SettingsActivity::class.java.simpleName
    }

    val mActivity = this
    val mToggleDialogKey = "TOGGLE_DIALOG_KEY"

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            UiUtils.showDialog(
                StringUtils.getString(R.string.confirm_restart_title, context),
                StringUtils.getString(R.string.confirm_restart, context),
                StringUtils.getString(R.string.restart_yes, context),
                StringUtils.getString(R.string.restart_no, context),
                mActivity, UiUtils::restartApp, UiUtils::dismissDialog)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mBroadcastReceiver, IntentFilter(mToggleDialogKey))
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
