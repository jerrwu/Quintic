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
    private val mDataList: List<CardEntity>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var onItemClick: ((CardEntity, Boolean) -> Unit)? = null
    var onItemLongClick: ((CardEntity) -> Boolean)? = null
    var useNightMode: Boolean = false
    var isMultiSelect = false
    var itemsSelected: ArrayList<CardEntity> = ArrayList()
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
                if (isMultiSelect) {
                    handleMultiSelectOnClick(adapterPosition)
                } else {
                    onItemClick?.invoke(mDataList[adapterPosition], false)
                    // transfer position to update selected  position in the fragment
                }
            }

            itemView.setOnLongClickListener {
                if (!isMultiSelect){
                    enterMultiSelect(adapterPosition)
                }
                true
            }
        }
    }

    fun handleMultiSelectOnClick(adapterPosition: Int) {
        val item = mDataList[adapterPosition]
        if (item.isSelected) {
            itemsSelected.remove(item)
        } else {
            itemsSelected.add(item)
        }
        item.isSelected = !item.isSelected
        if (itemsSelected.size == 0) {
            isMultiSelect = false
            onItemClick?.invoke(mDataList[adapterPosition], true)
        }
        notifyDataSetChanged()
    }

    fun enterMultiSelect(adapterPosition: Int) {
        val item = mDataList[adapterPosition]
        isMultiSelect = true
        itemsSelected.add(item)
        item.isSelected = true
        onItemLongClick?.invoke(item)
        notifyDataSetChanged()
    }
}