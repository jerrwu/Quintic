package com.jerrwu.quintic.entry.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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

    var onCrossButtonClickListener: ((Int) -> Unit)? = null

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag: String = mDataList[position]

        (mContext as Activity).runOnUiThread {
            holder.tagTextView.text = tag
            holder.tagCrossButton.setOnClickListener {
                onCrossButtonClickListener?.invoke(position)
            }
        }
    }

    fun updateTags(tags: List<String>) {
        mDataList = tags
    }

    @UiThread
    inner class TagViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        internal var tagTextView: TextView = itemView.findViewById(R.id.tag_row_text)
        internal var tagCrossButton: ImageButton = itemView.findViewById(R.id.tag_cross_button)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(null, mDataList[adapterPosition], null)
            }
        }
    }
}