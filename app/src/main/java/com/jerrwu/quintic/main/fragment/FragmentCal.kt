package com.jerrwu.quintic.main.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import com.jerrwu.quintic.entities.cell.CellEntity
import com.jerrwu.quintic.R
import kotlinx.android.synthetic.main.fragment_cal.*
import kotlinx.android.synthetic.main.grid_cell.view.*


class FragmentCal : Fragment() {
    var adapter: CellAdapter? = null
    var cellList = ArrayList<CellEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cellList.add(CellEntity("0"))
        cellList.add(CellEntity("1"))
        cellList.add(CellEntity("2"))
        cellList.add(CellEntity("3"))
        cellList.add(CellEntity("4"))
        cellList.add(CellEntity("5"))
        cellList.add(CellEntity("6"))
        cellList.add(CellEntity("7"))
        cellList.add(CellEntity("8"))
        cellList.add(CellEntity("9"))
        cellList.add(CellEntity("10"))
        cellList.add(CellEntity("11"))
        cellList.add(CellEntity("h"))
        cellList.add(CellEntity("e"))
        cellList.add(CellEntity("h"))
        cellList.add(CellEntity("e"))
        cellList.add(CellEntity("l"))
        cellList.add(CellEntity("i"))
        cellList.add(CellEntity("x"))
        adapter =
            CellAdapter(
                context!!,
                cellList
            )

        grid_view_main.adapter = adapter
    }

    class CellAdapter: BaseAdapter {
        var cellList = ArrayList<CellEntity>()
        var context: Context? = null

        constructor(context: Context, cellList: ArrayList<CellEntity>) : super() {
            this.context = context
            this.cellList = cellList
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

            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var cellView = inflater.inflate(R.layout.grid_cell, parent, false)
            if (cell.text == "0") {
                cellView.gridCellText.text = ""
            } else {
                cellView.gridCellText.text = cell.text!!
            }

            return cellView
        }
    }
}
