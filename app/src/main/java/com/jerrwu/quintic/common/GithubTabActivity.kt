package com.jerrwu.quintic.common

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.jerrwu.quintic.R

class GithubTabActivity : BaseCustomTabActivity() {
    companion object {
        val TAG = GithubTabActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        url = getString(R.string.github_link_main)
        super.onCreate(savedInstanceState)
    }
}
