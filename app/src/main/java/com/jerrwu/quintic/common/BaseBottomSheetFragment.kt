package com.jerrwu.quintic.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jerrwu.quintic.utils.UiUtils

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {
    companion object {
        val TAG = BaseBottomSheetFragment::class.java.simpleName
    }

    private var mFragmentView: View? = null
    protected abstract val mLayoutRes: Int
    protected abstract val mLayoutId: Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent?.parent?.parent as View).fitsSystemWindows = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentView = inflater.inflate(mLayoutRes, container, false)
        return mFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = activity?.findViewById<View>(mLayoutId)
        if (UiUtils.hasNavBar(activity)) {
            layout?.setPadding(0, 0, 0, 128)
        }
    }

    protected fun closeSheet() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }
}