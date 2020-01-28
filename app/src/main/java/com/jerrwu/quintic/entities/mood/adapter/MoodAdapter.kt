package com.jerrwu.quintic.entities.mood.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.mood.MoodEntity
import kotlinx.android.synthetic.main.mood.view.*

class MoodAdapter(
    private val mDataList: List<MoodEntity>,
    private val context: Context,
    private val selected: MoodEntity?) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood: MoodEntity = mDataList[position]

        holder.moodIcon.setImageResource(mood.icOutline)
        if (mood == selected) {
            holder.moodIndicator.setColorFilter(ContextCompat.getColor(context, mood.color), PorterDuff.Mode.SRC_ATOP)
            holder.moodIndicator.moodSelectedIndicator.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var moodIcon: ImageButton = itemView.findViewById(R.id.moodIcon)
        internal var moodIndicator: ImageView = itemView.findViewById(R.id.moodSelectedIndicator)

        init {
            itemView.setOnClickListener {
            }
        }
    }
}