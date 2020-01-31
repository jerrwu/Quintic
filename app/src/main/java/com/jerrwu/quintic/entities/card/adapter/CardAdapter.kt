package com.jerrwu.quintic.entities.card.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.card.CardEntity
import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.main.MainActivity
import kotlinx.android.synthetic.main.card.view.*
import java.time.format.DateTimeFormatter


class CardAdapter(
    private val mDataList: List<CardEntity>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var onItemClick: ((CardEntity, Boolean) -> Unit)? = null
    var onItemLongClick: ((CardEntity) -> Boolean)? = null
    var mContext: Context? = null
    var isMultiSelect = false
    var itemsSelected: ArrayList<CardEntity> = ArrayList()
    private var selectedBg = R.color.colorQuad  // colorQuad
    private var unselectedBg = R.color.colorMain // colorMain

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card: CardEntity = mDataList[position]
        val mood: MoodEntity? = card.mood
        val context = mContext
        if (context != null) {
            if (mood != null && mood != MoodEntity.NONE) {
                holder.cardMood.visibility = View.VISIBLE
                holder.cardMood.text = mood.name
            } else {
                holder.cardMood.visibility = View.GONE
            }

            if (card.isSelected) {
                holder.cardRvBackground.setCardBackgroundColor(
                    ContextCompat.getColor(context, selectedBg)
                )
                setCardSelectedTextColor(holder.cardView)
            } else {
                holder.cardRvBackground.setCardBackgroundColor(
                    ContextCompat.getColor(context, unselectedBg)
                )
                setCardUnselectedTextColor(holder.cardView)
            }
            holder.cardRvBackground.setCardBackgroundColor(
                if (card.isSelected) ContextCompat.getColor(context, selectedBg)
                else ContextCompat.getColor(context, unselectedBg)
            )
        }

        holder.cardTitle.text = card.title
        holder.cardDate.text = DateTimeFormatter.ofPattern("MMM dd, yyyy").format(card.time)
        holder.cardContent.text = card.content

        val ic = card.ic
        if (ic != null) {
            if (ic == 0) {
                holder.cardIcHolder.visibility = View.GONE
                val params = holder.cardTextContainer.layoutParams as ConstraintLayout.LayoutParams
                params.marginEnd = 0
                holder.cardTextContainer.layoutParams = params
            } else {
                holder.cardIc.setImageResource(ic)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cardRvBackground: CardView = itemView.findViewById(R.id.card_rv_background)
        internal var cardTitle: TextView = itemView.findViewById(R.id.card_title)
        internal var cardDate: TextView = itemView.findViewById(R.id.card_date)
        internal var cardContent: TextView = itemView.findViewById(R.id.card_content)
        internal var cardMood: TextView = itemView.findViewById(R.id.card_mood)
        internal var cardIc: ImageView = itemView.findViewById(R.id.card_ic)
        internal var cardIcHolder: CardView = itemView.findViewById(R.id.card_ic_holder)
        internal var cardView: CardView = itemView as CardView
        internal var cardTextContainer: ConstraintLayout = itemView.findViewById(R.id.card_text_container)

        init {
            itemView.setOnClickListener {
                if (isMultiSelect) {
                    handleMultiSelectOnClick(adapterPosition, itemView)
                } else {
                    onItemClick?.invoke(mDataList[adapterPosition], false)
                    // transfer position to update selected  position in the fragment
                }
            }

            itemView.setOnLongClickListener {
                if (mContext !is MainActivity) {
                    return@setOnLongClickListener false
                }
                if (!isMultiSelect){
                    enterMultiSelect(adapterPosition, itemView)
                }
                true
            }
        }
    }

    fun handleMultiSelectOnClick(adapterPosition: Int, itemView: View) {
        val item = mDataList[adapterPosition]
        val view: CardView = itemView as CardView
        val context = itemView.context

        if (context != null) {
            if (item.isSelected) {
                itemsSelected.remove(item)
                view.setCardBackgroundColor(ContextCompat.getColor(context, unselectedBg))
                setCardUnselectedTextColor(view)
            } else {
                itemsSelected.add(item)
                view.setCardBackgroundColor(ContextCompat.getColor(context, selectedBg))
                setCardSelectedTextColor(view)
            }
        }
        item.isSelected = !item.isSelected
        if (itemsSelected.size == 0) {
            isMultiSelect = false
            onItemClick?.invoke(mDataList[adapterPosition], true)
        }
    }

    fun enterMultiSelect(adapterPosition: Int, itemView: View) {
        val item = mDataList[adapterPosition]
        val view = (itemView as CardView)
        val context = itemView.context

        isMultiSelect = true
        itemsSelected.add(item)
        if (context != null) {
            view.setCardBackgroundColor(ContextCompat.getColor(context, selectedBg))
        }

        setCardSelectedTextColor(view)
        item.isSelected = true
        onItemLongClick?.invoke(item)
        notifyDataSetChanged()
    }

    private fun setCardSelectedTextColor(view: CardView) {
        view.card_title.setTextColor(ContextCompat.getColor(view.context, R.color.colorTertiary))
        view.card_content.setTextColor(ContextCompat.getColor(view.context, R.color.colorTertiary))
        view.card_date.setTextColor(ContextCompat.getColor(view.context, R.color.colorTertiary))
        view.card_mood.setTextColor(ContextCompat.getColor(view.context, R.color.colorTertiary))
//        view.card_date.background.alpha = 25
//        // http://online.sfsu.edu/chrism/hexval.html for percent values
        view.card_select_check.visibility = View.VISIBLE
    }

    private fun setCardUnselectedTextColor(view: CardView) {
        view.card_title.setTextColor(ContextCompat.getColor(view.context, R.color.colorSecondary))
        view.card_content.setTextColor(ContextCompat.getColor(view.context, R.color.colorSecondary))
        view.card_date.setTextColor(ContextCompat.getColor(view.context, R.color.colorSecondary))
        view.card_mood.setTextColor(ContextCompat.getColor(view.context, R.color.colorSecondary))
//        view.card_date.background.alpha = 50
//        // http://online.sfsu.edu/chrism/hexval.html for percent values
        view.card_select_check.visibility = View.GONE
    }
}