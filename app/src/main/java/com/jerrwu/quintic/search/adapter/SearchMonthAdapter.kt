package com.jerrwu.quintic.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.time.Month

class SearchMonthAdapter(
    private val mDataList: List<Month>) : RecyclerView.Adapter<SearchMonthAdapter.MonthViewHolder>() {

    var onItemClick: ((Month) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.month_category_card, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month: Month = mDataList[position]

        holder.monthTextView.text = month.toString()
        holder.monthMiniTextView.text = month.number.toString()
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

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