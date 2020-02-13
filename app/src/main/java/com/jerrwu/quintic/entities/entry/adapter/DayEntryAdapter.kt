package com.jerrwu.quintic.entities.entry.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.entry.EntryEntity

class DayEntryAdapter(
    private val mDataList: List<EntryEntity>,
    private val mContext: Context
) : RecyclerView.Adapter<DayEntryAdapter.DayEntryViewHolder>() {

    var onItemClick: ((EntryEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayEntryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hours_category_card, parent, false)
        return DayEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayEntryViewHolder, position: Int) {
        val entry: EntryEntity = mDataList[position]

        (mContext as Activity).runOnUiThread {
//            holder.hoursCardTextView.text = entry.title
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @UiThread
    inner class DayEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        internal var hoursCardImageView: ImageView = itemView.findViewById(R.id.hoursCategoryCardImageView)
//        internal var hoursCardTextView: TextView = itemView.findViewById(R.id.hoursCategoryCardTextView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mDataList[adapterPosition])
            }
        }
    }
}