package com.jerrwu.quintic.common.base

import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.jerrwu.quintic.R

abstract class BaseCustomTabActivity : BaseActivity() {
    companion object {
        val TAG = BaseCustomTabActivity::class.java.simpleName
    }

    protected var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = Uri.parse(url)

        CustomTabsIntent.Builder()
            .setToolbarColor(
                ContextCompat.getColor(this,
                    R.color.colorPrimary
                ))
            .setShowTitle(true)
            .build()
            .launchUrl(this, uri)
        finish()
    }
}