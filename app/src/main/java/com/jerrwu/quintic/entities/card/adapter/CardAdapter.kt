package com.jerrwu.quintic.entities.card.adapter

import android.graphics.Color
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


class CardAdapter(
    private val mDataList: ArrayList<CardEntity>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var onItemClick: ((CardEntity) -> Unit)? = null
    var onItemLongClick: (((CardEntity), Int) -> Boolean)? = null
    var useNightMode: Boolean = false
    private var isMultiselect = false
    private var itemsSelected = 0
    private var selectedBg = 0 // colorQuad
    private var unselectedBg = 0 // colorMain

    private fun setColors() {
        if (useNightMode) {
            selectedBg = Color.parseColor("#363636")
            unselectedBg = Color.parseColor("#242424")
        } else {
            selectedBg = Color.parseColor("#E7E7E7")
            unselectedBg = Color.parseColor("#FBFBFB")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        setColors()
        holder.cardRvBackground.setCardBackgroundColor(if (mDataList[position].isSelected) selectedBg else unselectedBg)
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
        internal var cardRvBackground: CardView = itemView.findViewById<View>(R.id.card_rv_background) as CardView
        internal var cardTitle: TextView = itemView.findViewById<View>(R.id.card_title) as TextView
        internal var cardContent: TextView = itemView.findViewById<View>(R.id.card_content) as TextView
        internal var cardIc: ImageView = itemView.findViewById<View>(R.id.card_ic) as ImageView
        internal var cardIcHolder: CardView = itemView.findViewById<View>(R.id.card_ic_holder) as CardView
        internal var cardTextContainer: ConstraintLayout = itemView.findViewById<View>(R.id.card_text_container) as ConstraintLayout

        init {
            itemView.setOnClickListener {
                if (isMultiselect) {
                    if (mDataList[adapterPosition].isSelected) {
                        itemsSelected --
                    } else {
                        itemsSelected ++
                    }
                    mDataList[adapterPosition].isSelected = !mDataList[adapterPosition].isSelected
                    if (itemsSelected == 0) {isMultiselect = false}
                    notifyDataSetChanged()
                } else {
                    onItemClick?.invoke(mDataList[adapterPosition])
                    // transfer position to update selected  position in the fragment
                }
            }

            itemView.setOnLongClickListener {
                if (!isMultiselect){
                    isMultiselect = true
                    itemsSelected ++
                    mDataList[adapterPosition].isSelected = true
                    onItemLongClick?.invoke(mDataList[adapterPosition], adapterPosition)!!
                    notifyDataSetChanged()
                }
                true
            }
        }
    }
}