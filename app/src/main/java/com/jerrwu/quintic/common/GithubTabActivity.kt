package com.jerrwu.quintic.common

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.base.BaseCustomTabActivity

class GithubTabActivity : BaseCustomTabActivity() {
    companion object {
        val TAG = GithubTabActivity::class.java.simpleName
    }

    override lateinit var mViewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        url = getString(R.string.github_link_main)
        super.onCreate(savedInstanceState)
    }
}
