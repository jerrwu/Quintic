package com.jerrwu.quintic.entry

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import com.jerrwu.quintic.BaseRecyclerViewHolder
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseRecyclerViewAdapter

class EntryTagAdapter(
    private var mDataList: List<String>,
    private val mContext: Context
) : BaseRecyclerViewAdapter<String, EntryTagAdapter.TagViewHolder>(mDataList, mContext) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tag_row, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag: String = mDataList[position]

        (mContext as Activity).runOnUiThread {
            holder.tagTextView.text = tag
        }
    }

    @UiThread
    inner class TagViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        internal var tagTextView: TextView = itemView.findViewById(R.id.tag_row_text)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(null, mDataList[adapterPosition], null)
            }
        }
    }
}