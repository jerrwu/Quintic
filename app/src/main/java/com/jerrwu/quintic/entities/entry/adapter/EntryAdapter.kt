package com.jerrwu.quintic.entities.entry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.BaseRecyclerViewHolder
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseRecyclerViewAdapter
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.main.MainActivity
import kotlinx.android.synthetic.main.entry_card.view.*
import java.time.format.DateTimeFormatter


class EntryAdapter(
    private val mDataList: List<EntryEntity>
) : BaseRecyclerViewAdapter<EntryEntity, EntryAdapter.EntryViewHolder>(mDataList, null) {
    companion object {
        val TAG = EntryAdapter::class.java.simpleName
    }

    var mContext: Context? = null
    var mIsMultiSelect = false
    var mItemsSelected: MutableList<EntryEntity> = ArrayList()
    var mItemsSelectedIds: MutableList<Int> = ArrayList()
    private var mSelectedBg = R.color.colorQuad  // colorQuad
    private var mUnselectedBg = R.color.colorMain // colorMain

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.entry_card, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry: EntryEntity = mDataList[position]
        val mood: MoodEntity? = entry.mood
        val context = mContext
        if (context != null) {
            if (mood != null && mood != MoodEntity.NONE) {
                holder.cardMood.visibility = View.VISIBLE
                holder.cardMood.text = mood.name
            } else {
                holder.cardMood.visibility = View.GONE
            }

            if (entry.isSelected) {
                holder.cardRvBackground.setCardBackgroundColor(
                    ContextCompat.getColor(context, mSelectedBg)
                )
                setCardSelectedTextColor(holder.cardView)
            } else {
                holder.cardRvBackground.setCardBackgroundColor(
                    ContextCompat.getColor(context, mUnselectedBg)
                )
                setCardUnselectedTextColor(holder.cardView)
            }
            holder.cardRvBackground.setCardBackgroundColor(
                if (entry.isSelected) ContextCompat.getColor(context, mSelectedBg)
                else ContextCompat.getColor(context, mUnselectedBg)
            )
        }

        holder.cardTitle.text = entry.title
        holder.cardDate.text = DateTimeFormatter.ofPattern("MMM dd, yyyy").format(entry.time)
        holder.cardContent.text = entry.content

        val ic = entry.ic
        if (ic == 0) {
            holder.cardIcHolder.visibility = View.GONE
            val params = holder.cardTextContainer.layoutParams as ConstraintLayout.LayoutParams
            params.marginEnd = 12
            holder.cardTextContainer.layoutParams = params
        } else {
            holder.cardIc.setImageResource(ic)
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class EntryViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
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
                if (mIsMultiSelect) {
                    handleMultiSelectOnClick(adapterPosition, itemView)
                } else {
                    onItemClick?.invoke(adapterPosition, mDataList[adapterPosition], false)
                    // transfer position to update selected  position in the fragment
                }
            }

            itemView.setOnLongClickListener {
                if (mContext !is MainActivity) {
                    return@setOnLongClickListener false
                }
                if (!mIsMultiSelect){
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
                mItemsSelected.remove(item)
                mItemsSelectedIds.remove(adapterPosition)
                view.setCardBackgroundColor(ContextCompat.getColor(context, mUnselectedBg))
                setCardUnselectedTextColor(view)
            } else {
                mItemsSelected.add(item)
                mItemsSelectedIds.add(adapterPosition)
                view.setCardBackgroundColor(ContextCompat.getColor(context, mSelectedBg))
                setCardSelectedTextColor(view)
            }
        }
        item.isSelected = !item.isSelected
        if (mItemsSelected.size == 0) {
            mIsMultiSelect = false
            onItemClick?.invoke(adapterPosition, mDataList[adapterPosition], true)
        }
    }

    fun enterMultiSelect(adapterPosition: Int, itemView: View) {
        val item = mDataList[adapterPosition]
        val view = (itemView as CardView)
        val context = itemView.context

        mIsMultiSelect = true
        mItemsSelected.add(item)
        mItemsSelectedIds.add(adapterPosition)
        if (context != null) {
            view.setCardBackgroundColor(ContextCompat.getColor(context, mSelectedBg))
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