package com.jerrwu.quintic.entities.mood.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.BaseRecyclerViewHolder
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseRecyclerViewAdapter
import com.jerrwu.quintic.entities.mood.MoodEntity
import kotlinx.android.synthetic.main.mood.view.*

class MoodAdapter(
    private val mDataList: List<MoodEntity>,
    private val mContext: Context
) : BaseRecyclerViewAdapter<MoodEntity, MoodAdapter.MoodViewHolder>(mDataList, mContext) {
    companion object {
        val TAG = MoodAdapter::class.java.simpleName
    }

    var selected: MoodEntity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood: MoodEntity = mDataList[position]

        holder.moodIcon.setImageResource(mood.icOutline)
        if (mood == selected) {
            holder.moodIndicator.setColorFilter(ContextCompat.getColor(mContext, mood.color), PorterDuff.Mode.SRC_ATOP)
            holder.moodIndicator.mood_selected_indicator.visibility = View.VISIBLE
        } else { holder.moodIndicator.mood_selected_indicator.visibility = View.GONE }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MoodViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        internal var moodIcon: ImageView = itemView.findViewById(R.id.mood_icon)
        internal var moodIndicator: ImageView = itemView.findViewById(R.id.mood_selected_indicator)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(null, mDataList[adapterPosition], null)
            }
        }
    }
}