package com.jerrwu.quintic.entities.entry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.jerrwu.quintic.common.base.BaseRecyclerViewHolder
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.base.BaseRecyclerViewAdapter
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
                holder.entryMood.visibility = View.VISIBLE
                holder.entryMood.text = mood.name
            } else {
                holder.entryMood.visibility = View.GONE
            }

            if (entry.isSelected) {
                holder.entrySelectedOverlay.visibility = View.VISIBLE
                showCheckIcon(holder.entryCardView)
            } else {
                holder.entrySelectedOverlay.visibility = View.GONE
                hideCheckIcon(holder.entryCardView)
            }
        }

        holder.entryTitle.text = entry.title
        holder.entryDate.text = DateTimeFormatter.ofPattern("MMM dd, yyyy").format(entry.time)
        holder.entryContent.text = entry.content

        val ic = entry.ic
        if (ic == 0) {
            holder.entryIcHolder.visibility = View.GONE
            val params = holder.entryTextContainer.layoutParams as ConstraintLayout.LayoutParams
            params.marginEnd = 12
            holder.entryTextContainer.layoutParams = params
        } else {
            holder.entryIc.setImageResource(ic)
        }

        if (entry.tags != null && entry.tags!!.isNotEmpty()) {
            holder.entryTagIndicator.visibility = View.VISIBLE
        } else {
            holder.entryTagIndicator.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class EntryViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        internal var entryTitle: TextView = itemView.findViewById(R.id.entry_title)
        internal var entryDate: TextView = itemView.findViewById(R.id.entry_date)
        internal var entryContent: TextView = itemView.findViewById(R.id.entry_content)
        internal var entryMood: TextView = itemView.findViewById(R.id.entry_mood)
        internal var entryIc: ImageView = itemView.findViewById(R.id.entry_ic)
        internal var entryIcHolder: CardView = itemView.findViewById(R.id.entry_ic_holder)
        internal var entrySelectedOverlay: ImageView = itemView.findViewById(R.id.entry_selected_overlay)
        internal var entryCardView: CardView = itemView as CardView
        internal var entryTextContainer: ConstraintLayout = itemView.findViewById(R.id.entry_text_container)
        internal var entryTagIndicator: ImageView = itemView.findViewById(R.id.entry_tag_indicator)

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
                view.entry_selected_overlay.visibility = View.INVISIBLE
                hideCheckIcon(view)
            } else {
                mItemsSelected.add(item)
                mItemsSelectedIds.add(adapterPosition)
                view.entry_selected_overlay.visibility = View.VISIBLE
                showCheckIcon(view)
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
            view.entry_selected_overlay.visibility = View.VISIBLE
        }

        showCheckIcon(view)
        item.isSelected = true
        onItemLongClick?.invoke(item)
        notifyDataSetChanged()
    }

    private fun showCheckIcon(view: CardView) {
        view.entry_select_check.visibility = View.VISIBLE
    }

    private fun hideCheckIcon(view: CardView) {
        view.entry_select_check.visibility = View.GONE
    }
}