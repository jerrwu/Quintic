package com.jerrwu.quintic.search.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import com.jerrwu.quintic.common.BaseRecyclerViewHolder
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseRecyclerViewAdapter
import com.jerrwu.quintic.entities.mood.MoodEntity

class SearchMoodAdapter(
    private val mDataList: List<MoodEntity>,
    private val mContext: Context
) : BaseRecyclerViewAdapter<MoodEntity, SearchMoodAdapter.SearchMoodViewHolder>(mDataList, mContext) {
    companion object {
        val TAG = SearchMoodAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMoodViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.mood_category_card, parent, false)
        return SearchMoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchMoodViewHolder, position: Int) {
        val mood: MoodEntity = mDataList[position]

        (mContext as Activity).runOnUiThread {
            holder.moodCardImageView.setBackgroundResource(mood.color)
            holder.moodCardImageView.setImageResource(mood.icOutline)

            holder.moodCardTextView.text = mood.name
        }
    }

    @UiThread
    inner class SearchMoodViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        internal var moodCardImageView: ImageView = itemView.findViewById(R.id.mood_category_card_imageview)
        internal var moodCardTextView: TextView = itemView.findViewById(R.id.mood_category_card_textview)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(null, mDataList[adapterPosition], null)
            }
        }
    }
}