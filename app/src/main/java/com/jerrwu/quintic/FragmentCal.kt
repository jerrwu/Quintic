package com.jerrwu.quintic


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_end.*
import kotlinx.android.synthetic.main.grid_cell.view.*


class FragmentCal : Fragment() {
    var adapter: CellAdapter? = null
    var cellList = ArrayList<Cell>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_end, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cellList.add(Cell("0"))
        cellList.add(Cell("1"))
        cellList.add(Cell("2"))
        cellList.add(Cell("3"))
        cellList.add(Cell("4"))
        cellList.add(Cell("5"))
        cellList.add(Cell("6"))
        cellList.add(Cell("7"))
        cellList.add(Cell("8"))
        cellList.add(Cell("9"))
        cellList.add(Cell("10"))
        cellList.add(Cell("11"))
        cellList.add(Cell("h"))
        cellList.add(Cell("e"))
        cellList.add(Cell("h"))
        cellList.add(Cell("e"))
        cellList.add(Cell("l"))
        cellList.add(Cell("i"))
        cellList.add(Cell("x"))
        adapter = CellAdapter(context!!, cellList)

        grid_view_main.adapter = adapter
    }

    class CellAdapter: BaseAdapter {
        var cellList = ArrayList<Cell>()
        var context: Context? = null

        constructor(context: Context, cellList: ArrayList<Cell>) : super() {
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
