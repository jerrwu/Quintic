package com.jerrwu.quintic.entities.cell.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.cell.CellEntity
import kotlinx.android.synthetic.main.cal_header_cell.view.*

class HeaderCellAdapter(
    override var context: Context?,
    override var cellList: List<CellEntity>) : CellAdapter(context, cellList) {
    companion object {
        val TAG = HeaderCellAdapter::class.java.simpleName
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val cell = this.cellList[position]
        val cellView: View

        cellView = if (convertView == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.cal_header_cell, parent, false)
        } else {
            convertView
        }

        (context as Activity).runOnUiThread {
            cellView.isClickable = false
            if (cell.text == "" || cell.text == "0") {
                cellView.isClickable = false
            } else {
                cellView.headerGridCellText.text = cell.text
            }
        }

        return cellView
    }
}