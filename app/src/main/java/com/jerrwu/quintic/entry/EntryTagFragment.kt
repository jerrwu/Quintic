package com.jerrwu.quintic.entry

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entry.adapter.EntryTagAdapter
import com.jerrwu.quintic.search.SearchActivity
import com.jerrwu.quintic.utils.StringUtils
import com.jerrwu.quintic.utils.UiUtils
import kotlinx.android.synthetic.main.tag_sheet.*

class EntryTagFragment(
    var tags: MutableList<String>
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
            mAdapter =
                EntryTagAdapter(tags, pContext)
            mAdapter.onCrossButtonClickListener = { position ->
                removeTag(position)
            }
            mAdapter.onItemClick = { _, tag, _ ->
                val intent = Intent(activity, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_TAG)
                intent.putExtra(SearchActivity.SEARCH_STRING, tag)
                intent.putExtra(SearchActivity.EXACT_SEARCH, true)
                startActivity(intent)
            }

            tags_recycler.layoutManager = LinearLayoutManager(pContext)
            tags_recycler.adapter = mAdapter
            tags_recycler.setHasFixedSize(true)

            tag_add_button.setOnClickListener {
                UiUtils.showEditTextDialog(
                    StringUtils.getString(R.string.new_tag, pContext),
                    StringUtils.getString(R.string.ok, pContext),
                    StringUtils.getString(R.string.cancel, pContext),
                    pContext,
                    this::addTag)
            }
        }
    }

    private fun removeTag(position: Int) {
        tags.removeAt(position)
        mAdapter.updateTags(tags)
        mAdapter.notifyItemRemoved(position)
        if (tags.size == 0) {
            (activity as EntryActivity).onAllTagsRemoved()
        }
    }

    private fun addTag(tag: String) {
        val i = tags.size
        tags.add(tag)
        mAdapter.updateTags(tags)
        mAdapter.notifyItemInserted(i)
        if (i == 0) {
            (activity as EntryActivity).onTagsNotEmpty()
        }
    }
}