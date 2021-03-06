package com.jerrwu.quintic.entities.cell.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.cell.CellEntity
import kotlinx.android.synthetic.main.cal_cell.view.*

open class CellAdapter(
    open var context: Context?,
    open var cellList: List<CellEntity>) : BaseAdapter() {
    companion object {
        val TAG = CellAdapter::class.java.simpleName
    }

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
                    cellView.grid_cell_text.setTextColor(ContextCompat.getColor(activity, R.color.colorSupport))
                    cellView.grid_cell_text.typeface = ResourcesCompat.getFont(activity, R.font.productsansbold)
                }
                cellView.grid_cell_text.text = cell.text
            }

            if (cell.number != 0) {
                if (cell.number == 1) {
                    cellView.cell_indicator.setImageResource(R.drawable.ic_check)
                } else {
                    cellView.cell_indicator.setImageResource(R.drawable.ic_check_all)
                }
            } else {
                cellView.cell_indicator.visibility = View.GONE
            }
        }

        return cellView
    }
}