package com.jerrwu.quintic.common.view.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.jerrwu.quintic.R
import kotlinx.android.synthetic.main.round_spinner_item.view.*

class CalSpinnerAdapter(
    context: Context,
    strings: List<String>): ArrayAdapter<String>(context, 0, strings) {
    companion object {
        val TAG = CalSpinnerAdapter::class.java.simpleName
    }

    // TODO: remove ripple in bg

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val text = getItem(position)
        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.round_spinner_item,
            parent,
            false
        )

        view.cal_spinner_textview.text = text
        return view
    }

}