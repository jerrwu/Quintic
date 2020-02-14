package com.jerrwu.quintic.entities.entry.adapter

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.mood.MoodEntity
import java.time.format.DateTimeFormatter

class DayEntryAdapter(
    private val mDataList: List<EntryEntity>,
    private val mContext: Context
) : RecyclerView.Adapter<DayEntryAdapter.DayEntryViewHolder>() {

    var onItemClick: ((EntryEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayEntryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cal_day_entry_item, parent, false)
        return DayEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayEntryViewHolder, position: Int) {
        val entry: EntryEntity = mDataList[position]

        (mContext as Activity).runOnUiThread {
//            holder.hoursCardTextView.text = entry.title
            holder.dayEntryTitle.text = entry.title
            holder.dayEntryDate.text = DateTimeFormatter.ofPattern("MMM dd, yyyy").format(entry.time)
            holder.dayEntryContent.text = entry.content

            val color = entry.mood?.color
            if (entry.mood != MoodEntity.NONE && color != null) {
                holder.dayEntryIndicator.setColorFilter(
                    ContextCompat.getColor(mContext, color),
                    PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @UiThread
    inner class DayEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var dayEntryTitle: TextView = itemView.findViewById(R.id.cal_day_title_text)
        internal var dayEntryDate: TextView = itemView.findViewById(R.id.cal_day_date_text)
        internal var dayEntryContent: TextView = itemView.findViewById(R.id.cal_day_content_text)
        internal var dayEntryIndicator: ImageView = itemView.findViewById(R.id.cal_day_item_color_indicator)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mDataList[adapterPosition])
            }
        }
    }
}