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
import com.jerrwu.quintic.entities.cell.CellEntity
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.time.DayEntity
import com.jerrwu.quintic.entities.time.MonthEntity
import com.jerrwu.quintic.entities.time.WeekdayEntity
import com.jerrwu.quintic.entities.time.YearEntity
import com.jerrwu.quintic.helpers.FileHelper
import com.jerrwu.quintic.helpers.GsonHelper
import kotlinx.android.synthetic.main.fragment_cal.*
import kotlinx.android.synthetic.main.cal_cell.view.*
import java.time.LocalDate


class FragmentCal : Fragment() {
    var mAdapter: CellAdapter? = null
    var mCellList = ArrayList<CellEntity>()
    var mAddSpacing = true
    var mYears: List<YearEntity>? = null
    var mCurrentYear: YearEntity? = null
    var mCurrentMonth: MonthEntity? = null

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

        mYears = Gson().fromJson<List<YearEntity>>(FileHelper.fromAssetsJson(activity as Context, "cal_test.json"),
            GsonHelper.YearListType)

        // Set weekday headers
        for (i in 1 until 8) {
            mCellList.add(
                CellEntity(WeekdayEntity(i).toShortString())
            )
        }

        onCalSelected(now.year, now.monthValue)
    }

    private fun onCalSelected(yearValue: Int, monthValue: Int) {
        // Set current year and month
        for (year: YearEntity in mYears.orEmpty()) {
            if (yearValue == year.number) {
                mCurrentYear = year
                break
            }
        }
        for (month: MonthEntity in mCurrentYear?.months.orEmpty()) {
            if (monthValue == month.number) {
                mCurrentMonth = month
                break
            }
        }

        // Load cellList
        Log.d("fragmentcal", mCurrentMonth?.days.toString())
        for (day: DayEntity in mCurrentMonth?.days.orEmpty()) {
            if (mAddSpacing) {
                mAddSpacing = false
                for (i in 1 until day.dayOfWeek) {
                    mCellList.add(CellEntity(""))
                }
            }
            mCellList.add(CellEntity(day.dayOfMonth.toString()))
        }

        fragmentCalMonthSelectText.text = mCurrentMonth.toString()
        fragmentCalYearText.text = mCurrentYear?.number.toString()

        mAdapter =
            CellAdapter(
                context,
                mCellList
            )
        calGrid.adapter = mAdapter
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
                inflater.inflate(R.layout.cal_cell, parent, false)
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
