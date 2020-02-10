package com.jerrwu.quintic.main.fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jerrwu.quintic.entities.cell.CellEntity
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.time.DayEntity
import com.jerrwu.quintic.entities.time.MonthEntity
import com.jerrwu.quintic.entities.time.YearEntity
import com.jerrwu.quintic.helpers.FileHelper
import kotlinx.android.synthetic.main.fragment_cal.*
import kotlinx.android.synthetic.main.grid_cell.view.*
import java.time.LocalDate


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

        val now = LocalDate.now()
        var currentYear = YearEntity(now.year, null)
        var currentMonth = MonthEntity(now.monthValue, null)

        val sType = object : TypeToken<List<YearEntity>>() { }.type

        val years = Gson().fromJson<List<YearEntity>>(FileHelper.fromAssetsJson(activity as Context, "cal_test.json"),
            sType)

        for (year: YearEntity in years) {
            if (currentYear.number == year.number) currentYear = year
            for (month: MonthEntity in year.months!!) {
                if (currentMonth.number == month.number) currentMonth = month
                for (day: DayEntity in month.days!!) {
                    cellList.add(CellEntity(day.dayOfMonth.toString()))
                }
            }
        }

        fragmentCalMonthSelectText.text = currentMonth.toString()
        fragmentCalYearText.text = currentYear.number.toString()

        adapter =
            CellAdapter(
                context,
                cellList
            )

        grid_view_main.adapter = adapter
    }

    class CellAdapter(var context: Context?, var cellList: ArrayList<CellEntity>) : BaseAdapter() {

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
                val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.grid_cell, parent, false)
            } else {
                convertView
            }

            if (cell.text == "0") {
                cellView.gridCellText.text = ""
            } else {
                cellView.gridCellText.text = cell.text
            }

            return cellView
        }
    }
}
