package com.jerrwu.quintic.search.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import com.jerrwu.quintic.common.BaseRecyclerViewHolder
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseRecyclerViewAdapter
import com.jerrwu.quintic.entities.time.MonthEntity

class SearchMonthAdapter(
    private val mDataList: List<MonthEntity>,
    private val mContext: Context
) : BaseRecyclerViewAdapter<MonthEntity, SearchMonthAdapter.MonthViewHolder>(mDataList, mContext) {
    companion object {
        val TAG = SearchMonthAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.month_category_card, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month: MonthEntity = mDataList[position]

        (mContext as Activity).runOnUiThread {
            holder.monthTextView.text = month.stringValue()
            holder.monthMiniTextView.text = month.number.toString()
        }
    }

    @UiThread
    inner class MonthViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        internal var monthTextView: TextView = itemView.findViewById(R.id.month_card_textview)
        internal var monthMiniTextView: TextView = itemView.findViewById(R.id.month_card_mini_textview)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(null, mDataList[adapterPosition], null)
            }
        }
    }
}