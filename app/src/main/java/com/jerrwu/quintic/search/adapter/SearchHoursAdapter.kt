package com.jerrwu.quintic.search.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.BaseRecyclerViewHolder
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseRecyclerViewAdapter

class SearchHoursAdapter(
    private val mDataList: List<String>,
    private val mContext: Context
) : BaseRecyclerViewAdapter<String, SearchHoursAdapter.HoursViewHolder>(mDataList, mContext) {
    companion object {
        val TAG = SearchHoursAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hours_category_card, parent, false)
        return HoursViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        val hoursName: String = mDataList[position]

        (mContext as Activity).runOnUiThread {
            holder.hoursCardTextView.text = hoursName
        }
    }

    @UiThread
    inner class HoursViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        internal var hoursCardImageView: ImageView = itemView.findViewById(R.id.hours_category_card_imageview)
        internal var hoursCardTextView: TextView = itemView.findViewById(R.id.hours_category_card_textview)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(null, mDataList[adapterPosition], null)
            }
        }
    }
}