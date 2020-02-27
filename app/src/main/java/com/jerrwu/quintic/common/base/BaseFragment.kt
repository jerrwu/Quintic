package com.jerrwu.quintic.common.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.jerrwu.quintic.common.interfaces.BaseFragmentInterface

abstract class BaseFragment : Fragment(), BaseFragmentInterface {
    companion object {
        val TAG = BaseFragment::class.java.simpleName
    }

    protected abstract val mViewModel: ViewModel
}