package com.jerrwu.quintic.common

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.jerrwu.quintic.R

class GithubTabActivity : AppCompatActivity() {
    companion object {
        val TAG = GithubTabActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = Uri.parse(getString(R.string.github_link_main))
        val intentBuilder = CustomTabsIntent.Builder()
        intentBuilder.setToolbarColor(ContextCompat.getColor(this,
            R.color.colorPrimary
        ))
        intentBuilder.setShowTitle(true)
        val customTabsIntent = intentBuilder.build()
        customTabsIntent.launchUrl(this, uri)
        finish()
    }
}
