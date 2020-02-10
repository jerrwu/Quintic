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
import com.jerrwu.quintic.entities.time.WeekdayEntity
import com.jerrwu.quintic.entities.time.YearEntity
import com.jerrwu.quintic.helpers.FileHelper
import com.jerrwu.quintic.helpers.GsonHelper
import kotlinx.android.synthetic.main.fragment_cal.*
import kotlinx.android.synthetic.main.grid_cell.view.*
import java.time.LocalDate


class FragmentCal : Fragment() {
    var adapter: CellAdapter? = null
    var cellList = ArrayList<CellEntity>()
    var addSpacing = true

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

        val years = Gson().fromJson<List<YearEntity>>(FileHelper.fromAssetsJson(activity as Context, "cal_test.json"),
            GsonHelper.YearListType)

        // Set weekday headers
        for (i in 1 until 8) {
            cellList.add(
                CellEntity(WeekdayEntity(i).toShortString())
            )
        }

        // Set current year and month
        for (year: YearEntity in years) {
            if (currentYear.number == year.number) currentYear = year
            break
        }
        for (month: MonthEntity in currentYear.months.orEmpty()) {
            if (currentMonth.number == month.number) currentMonth = month
            break
        }

        // Load cellList
        for (day: DayEntity in currentMonth.days.orEmpty()) {
            if (addSpacing) {
                addSpacing = false
                for (i in 1 until day.dayOfWeek) {
                    cellList.add(CellEntity(""))
                }
            }
            cellList.add(CellEntity(day.dayOfMonth.toString()))
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

            if (cell.text == "" || cell.text == "0") {
                cellView.isClickable = false
            } else {
                cellView.gridCellText.text = cell.text
            }

            return cellView
        }
    }
}
