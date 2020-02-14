package com.jerrwu.quintic.common

import androidx.fragment.app.Fragment
import com.jerrwu.quintic.common.listeners.BaseFragmentInterface

abstract class BaseFragment : Fragment(), BaseFragmentInterface {
    companion object {
        val TAG = BaseFragment::class.java.simpleName
    }
}