package com.jerrwu.quintic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


class CardAdapter(private val mDataList: ArrayList<Card>) : RecyclerView.Adapter<CardAdapter.MyViewHolder>() {
    var onItemClick: ((Card) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardTitle.text = mDataList[position].title
        holder.cardContent.text = mDataList[position].content
        val ic = mDataList[position].ic
        if (ic == 0) {
            holder.cardIcHolder.visibility = View.GONE
            val params = holder.cardTextContainer.layoutParams as ConstraintLayout.LayoutParams
            params.marginEnd = 0
            holder.cardTextContainer.layoutParams = params
        }
        else {
            mDataList[position].ic?.let { holder.cardIc.setImageResource(it) }
        }
        }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cardTitle: TextView = itemView.findViewById<View>(R.id.card_title) as TextView
        internal var cardContent: TextView = itemView.findViewById<View>(R.id.card_content) as TextView
        internal var cardIc: ImageView = itemView.findViewById<View>(R.id.card_ic) as ImageView
        internal var cardIcHolder: CardView = itemView.findViewById<View>(R.id.card_ic_holder) as CardView
        internal var cardTextContainer: ConstraintLayout = itemView.findViewById<View>(R.id.card_text_container) as ConstraintLayout

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mDataList[adapterPosition])
            }
        }
    }
}