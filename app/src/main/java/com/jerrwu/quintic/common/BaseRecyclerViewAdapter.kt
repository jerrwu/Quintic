package com.jerrwu.quintic.common

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.BaseRecyclerViewHolder

abstract class BaseRecyclerViewAdapter<T, VH : BaseRecyclerViewHolder>(
    private val dataList: List<T>,
    private val context: Context?
) : RecyclerView.Adapter<VH>() {

    companion object {
        val TAG = BaseRecyclerViewAdapter::class.java.simpleName
    }

    var onItemClick: ((Int?, T, Boolean?) -> Unit)? = null
    var onItemLongClick: ((T) -> Boolean)? = null

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    abstract override fun onBindViewHolder(holder: VH, position: Int)

    override fun getItemCount(): Int {
        return dataList.size
    }

}