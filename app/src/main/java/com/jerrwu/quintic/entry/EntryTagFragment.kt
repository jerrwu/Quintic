package com.jerrwu.quintic.entry

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseBottomSheetFragment
import com.jerrwu.quintic.entry.adapter.EntryTagAdapter
import com.jerrwu.quintic.search.SearchActivity
import com.jerrwu.quintic.utils.StringUtils
import com.jerrwu.quintic.utils.UiUtils
import kotlinx.android.synthetic.main.tag_sheet.*

class EntryTagFragment(
    var tags: MutableList<String>
) : BaseBottomSheetFragment() {
    companion object {
        val TAG = EntryTagFragment::class.java.simpleName
    }

    private lateinit var mAdapter: EntryTagAdapter
    override val mLayoutRes: Int = R.layout.tag_sheet
    override val mLayoutId: Int = R.id.tags_sheet_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pContext = context
        if (pContext != null) {
            mAdapter =
                EntryTagAdapter(tags, pContext)
            mAdapter.onDeleteButtonClickListener = { position ->
                removeTag(position)
            }
            mAdapter.onItemClick = { _, tag, _ ->
                val intent = Intent(activity, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_TAG)
                intent.putExtra(SearchActivity.SEARCH_STRING, tag)
                intent.putExtra(SearchActivity.EXACT_SEARCH, false)
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
        } else {
            (activity as EntryActivity).onTagsChanged()
        }
    }

    private fun addTag(tag: String) {
        if (tag in tags) return

        val i = tags.size
        tags.add(tag)
        mAdapter.updateTags(tags)
        mAdapter.notifyItemInserted(i)
        if (i == 0) {
            (activity as EntryActivity).onTagsNotEmpty()
        }else {
            (activity as EntryActivity).onTagsChanged()
        }
    }
}