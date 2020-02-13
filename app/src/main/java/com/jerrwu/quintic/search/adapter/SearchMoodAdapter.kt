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
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.mood.MoodEntity

class SearchMoodAdapter(
    private val mDataList: List<MoodEntity>,
    val mContext: Context
) : RecyclerView.Adapter<SearchMoodAdapter.MoodViewHolder>() {

    var onItemClick: ((MoodEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.mood_category_card, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood: MoodEntity = mDataList[position]

        (mContext as Activity).runOnUiThread {
            holder.moodCardImageView.setBackgroundResource(mood.color)
            holder.moodCardImageView.setImageResource(mood.icOutline)

            holder.moodCardTextView.text = mood.name
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @UiThread
    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var moodCardImageView: ImageView = itemView.findViewById(R.id.moodCategoryCardImageView)
        internal var moodCardTextView: TextView = itemView.findViewById(R.id.moodCategoryCardTextView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mDataList[adapterPosition])
            }
        }
    }
}