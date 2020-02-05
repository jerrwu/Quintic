package com.jerrwu.quintic.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R

class SearchHoursAdapter(
    private val mDataList: List<String>) : RecyclerView.Adapter<SearchHoursAdapter.HoursViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hours_category_card, parent, false)
        return HoursViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        val hoursName: String = mDataList[position]

        holder.hoursCardTextView.text = hoursName
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class HoursViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var hoursCardImageView: ImageView = itemView.findViewById(R.id.hoursCategoryCardImageView)
        internal var hoursCardTextView: TextView = itemView.findViewById(R.id.hoursCategoryCardTextView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mDataList[adapterPosition])
            }
        }
    }
}