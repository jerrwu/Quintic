package com.jerrwu.quintic.entities.card.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.card.CardEntity


class CardAdapter(private val mDataList: ArrayList<CardEntity>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    var onItemClick: ((CardEntity) -> Unit)? = null
    var onItemLongClick: ((CardEntity) -> Boolean)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
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

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cardTitle: TextView = itemView.findViewById<View>(R.id.card_title) as TextView
        internal var cardContent: TextView = itemView.findViewById<View>(R.id.card_content) as TextView
        internal var cardIc: ImageView = itemView.findViewById<View>(R.id.card_ic) as ImageView
        internal var cardIcHolder: CardView = itemView.findViewById<View>(R.id.card_ic_holder) as CardView
        internal var cardTextContainer: ConstraintLayout = itemView.findViewById<View>(
            R.id.card_text_container
        ) as ConstraintLayout

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mDataList[adapterPosition])
            }

            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(mDataList[adapterPosition])!!
            }
        }
    }
}