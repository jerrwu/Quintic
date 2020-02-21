package com.jerrwu.quintic

import android.view.View
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView

@UiThread
abstract class BaseRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)