package com.jerrwu.quintic.search.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.time.MonthEntity

class SearchMonthAdapter(
    private val mDataList: List<MonthEntity>,
    private val mContext: Context
) : RecyclerView.Adapter<SearchMonthAdapter.MonthViewHolder>() {

    var onItemClick: ((MonthEntity) -> Unit)? = null

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

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @UiThread
    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var monthTextView: TextView = itemView.findViewById(R.id.monthCardTextView)
        internal var monthMiniTextView: TextView = itemView.findViewById(R.id.monthCardMiniTextView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mDataList[adapterPosition])
            }
        }
    }
}