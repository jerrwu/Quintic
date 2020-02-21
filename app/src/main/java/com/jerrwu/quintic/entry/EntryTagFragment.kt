package com.jerrwu.quintic.entry

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jerrwu.quintic.R
import com.jerrwu.quintic.utils.UiUtils
import kotlinx.android.synthetic.main.nav_sheet.*
import kotlinx.android.synthetic.main.tag_sheet.*

class EntryTagFragment(
    var tags: List<String>
) : BottomSheetDialogFragment() {
    companion object {
        val TAG = EntryTagFragment::class.java.simpleName
    }

    private var mFragmentView: View? = null
    private lateinit var mAdapter: EntryTagAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view!!.parent.parent.parent as View).fitsSystemWindows = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentView = inflater.inflate(R.layout.tag_sheet, container, false)
        return mFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (UiUtils.hasNavBar(activity)) {
            tags_sheet_layout.setPadding(0, 0, 0, 128)
        }

        val pContext = context
        if (pContext != null) {
            mAdapter = EntryTagAdapter(listOf("tag1", "tag2", "tag3"), pContext)
            tags_recycler.layoutManager = LinearLayoutManager(pContext)
            tags_recycler.adapter = mAdapter
            tags_recycler.setHasFixedSize(true)
        }
    }

    fun updateTagsData(newTags: List<String>) {
        tags = newTags
    }
}