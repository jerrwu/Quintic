package com.jerrwu.quintic.entities.cell.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.cell.CellEntity
import kotlinx.android.synthetic.main.cal_cell.view.*

open class CellAdapter(
    open var context: Context?,
    open var cellList: List<CellEntity>) : BaseAdapter() {

    override fun getCount(): Int {
        return cellList.size
    }

    override fun getItem(position: Int): Any {
        return cellList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val cell = this.cellList[position]
        val cellView: View

        cellView = if (convertView == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.cal_cell, parent, false)
        } else {
            convertView
        }

        val activity = context as Activity
        activity.runOnUiThread {
            if (cell.text == "" || cell.text == "0") {
                cellView.isClickable = false
            } else {
                if (cell.text in ConstantLists.calHeaders) {
                    // header text
                    cellView.isClickable = false
                    cellView.gridCellText.setTextColor(ContextCompat.getColor(activity, R.color.colorTertiary))
                    cellView.gridCellText.textSize = 13F
                }
                cellView.gridCellText.text = cell.text
            }

            if (cell.number != 0) {
                cellView.testindictext.text = cell.number.toString()
            } else {
                cellView.testindictext.visibility = View.GONE
            }
        }

        return cellView
    }
}