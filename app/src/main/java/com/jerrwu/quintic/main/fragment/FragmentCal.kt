package com.jerrwu.quintic.main.fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.jerrwu.quintic.entities.cell.CellEntity
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.view.CalSpinnerAdapter
import com.jerrwu.quintic.entities.time.DayEntity
import com.jerrwu.quintic.entities.time.MonthEntity
import com.jerrwu.quintic.entities.time.WeekdayEntity
import com.jerrwu.quintic.entities.time.YearEntity
import com.jerrwu.quintic.helpers.FileHelper
import com.jerrwu.quintic.helpers.GsonHelper
import com.jerrwu.quintic.helpers.StringHelper
import kotlinx.android.synthetic.main.fragment_cal.*
import kotlinx.android.synthetic.main.cal_cell.view.*
import java.time.LocalDate
import java.util.stream.Collectors


class FragmentCal : Fragment() {
    var mAdapter: CellAdapter? = null
    var mCellList = ArrayList<CellEntity>()
    var mAddSpacing = true
    var mYears: List<YearEntity>? = null
    var mCurrentYear: YearEntity? = null
    var mCurrentMonth: MonthEntity? = null
    var mWeekdayLabels = ArrayList<CellEntity>()
    var mCurrentMonthValue: Int = 0
    var mCurrentYearValue: Int = 0
    private var mContext: Context? = null

    private val mMonthSpinnerList: ArrayList<String> = ArrayList()
    private val mYearSpinnerList: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set weekday headers
        for (i in 1 until 8) {
            mWeekdayLabels.add(
                CellEntity(WeekdayEntity(i).toShortString())
            )
        }
        mContext = context

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val now = LocalDate.now()

        mYears = Gson().fromJson<List<YearEntity>>(FileHelper.fromAssetsJson(activity as Context, "cal_test.json"),
            GsonHelper.YearListType)

        onCalSelected(now.year, now.monthValue)

        fragmentCalSelectionSpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val selection = mMonthSpinnerList[position]
                onCalSelected(mCurrentYearValue, StringHelper.intOfMonth(selection))
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // no-op
            }
        }

        selectorBox.setOnClickListener {
            fragmentCalSelectionSpinner.performClick()
        }

        fragmentCalBtnBackward.setOnClickListener {
            val newYear: Int
            val newMonth: Int

            if (mCurrentMonthValue == 1) {
                newMonth = 12
                newYear = mCurrentYearValue - 1
            } else {
                newMonth = mCurrentMonthValue - 1
                newYear = mCurrentYearValue
            }

            onCalSelected(newYear, newMonth)
        }

        fragmentCalBtnForward.setOnClickListener {
            val newYear: Int
            val newMonth: Int

            if (mCurrentMonthValue == 12) {
                newMonth = 1
                newYear = mCurrentYearValue + 1
            } else {
                newMonth = mCurrentMonthValue + 1
                newYear = mCurrentYearValue
            }

            onCalSelected(newYear, newMonth)
        }
    }

    private fun setupMonthSpinner() {
        val context = mContext
        if (context != null) {
            fragmentCalSelectionSpinner.adapter = null
            mMonthSpinnerList.clear()

            for (month in mCurrentYear?.months.orEmpty()) {
                mMonthSpinnerList.add(month.toString())
            }

            val spinnerAdapter = CalSpinnerAdapter(
                context, mMonthSpinnerList)

            fragmentCalSelectionSpinner.adapter = spinnerAdapter
            spinnerAdapter.setDropDownViewResource(R.layout.round_spinner_item)
        }
    }

    private fun onCalSelected(yearValue: Int, monthValue: Int) {
        // Set current year and month
        var selectedYearExists = false
        var selectedMonthExists = false

        // Reset cellList
        mAddSpacing = true
        mCellList.clear()
        mCellList.addAll(mWeekdayLabels)

        for (year: YearEntity in mYears.orEmpty()) {
            if (yearValue == year.number) {
                mCurrentYear = year
                mCurrentYearValue = year.number
                selectedYearExists = true
                break
            }
        }
        for (month: MonthEntity in mCurrentYear?.months.orEmpty()) {
            if (monthValue == month.number) {
                mCurrentMonth = month
                mCurrentMonthValue = month.number
                selectedMonthExists = true
                break
            }
        }

        if (!selectedYearExists || !selectedMonthExists) {
            StringHelper.makeSnackbar(StringHelper.getString(R.string.month_year_not_exist, context),
                activity)
            return
        }

        // Load cellList
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
        setupMonthSpinner()
        return
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
