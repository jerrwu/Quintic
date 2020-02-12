package com.jerrwu.quintic.main.fragment


import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.view.widget.CalSpinnerAdapter
import com.jerrwu.quintic.entities.cell.CellEntity
import com.jerrwu.quintic.entities.time.DayEntity
import com.jerrwu.quintic.entities.time.MonthEntity
import com.jerrwu.quintic.entities.time.WeekdayEntity
import com.jerrwu.quintic.entities.time.YearEntity
import com.jerrwu.quintic.helpers.*
import kotlinx.android.synthetic.main.cal_cell.view.*
import kotlinx.android.synthetic.main.fragment_cal.*
import java.time.LocalDate


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

    private val mMonthSpinnerList: ArrayList<String> = ArrayList()
    private val mYearSpinnerList: ArrayList<String> = ArrayList()

    /*
    TODO: improve overall fragment performance, fix bug with indicator not updating
     */

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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentCalSelectionSpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val monthValue = StringHelper.intOfMonth(mMonthSpinnerList[position])
                if (monthValue != mCurrentMonthValue){
                    AsyncTask.execute {
                        onCalSelected(mCurrentYearValue, monthValue)
                    }
                }
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

            AsyncTask.execute { onCalSelected(newYear, newMonth) }
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

            AsyncTask.execute { onCalSelected(newYear, newMonth) }
        }
    }

    override fun onResume() {
        super.onResume()
        AsyncTask.execute {
            val now = LocalDate.now()
            mYears = Gson().fromJson<List<YearEntity>>(FileHelper.fromAssetsJson(activity as Context, "cal_test.json"),
                GsonHelper.YearListType)

            if (mCurrentYearValue == 0 || mCurrentMonthValue == 0) {
                onCalSelected(now.year, now.monthValue)
            } else {
                onCalSelected(mCurrentYearValue, mCurrentMonthValue)
            }
        }
    }

    private fun setupMonthSpinner() {
        val context = activity
        if (context != null) {
            mMonthSpinnerList.clear()

            for (month in mCurrentYear?.months.orEmpty()) {
                mMonthSpinnerList.add(month.toString())
            }

            val spinnerAdapter = CalSpinnerAdapter(context, mMonthSpinnerList)

            activity?.runOnUiThread {
                fragmentCalSelectionSpinner.adapter = spinnerAdapter
            }
        }
    }

    private fun onCalSelected(yearValue: Int, monthValue: Int) {
        // Set current year and month
        var selectedYearExists = false
        var selectedMonthExists = false

        var yearChanged = false
        if (mCurrentYearValue != yearValue) {
            yearChanged = true
        }

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
            val searchDate = mCurrentYearValue.toString() + mCurrentMonthValue.toString() + day.dayOfMonth
            val calDbHelper = activity?.let { CalDbHelper(it) }
            val result = calDbHelper?.let {
                SearchHelper.performCalEntryCountSearch(searchDate, it)
            }
            mCellList.add(CellEntity(day.dayOfMonth.toString(), result?.get(1)))
        }

        mAdapter =
            CellAdapter(
                context,
                mCellList
            )

        activity?.runOnUiThread {
            calGrid.adapter = mAdapter
            fragmentCalMonthSelectText.text = mCurrentMonth.toString()
            fragmentCalYearText.text = mCurrentYear?.number.toString()
        }

        if (yearChanged)
        setupMonthSpinner()
        val index = mMonthSpinnerList.indexOf(mCurrentMonth.toString())
        activity?.runOnUiThread {
            fragmentCalSelectionSpinner.setSelection(index)
        }
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

            (context as Activity).runOnUiThread {
                if (cell.text == "" || cell.text == "0") {
                    cellView.isClickable = false
                } else {
                    cellView.gridCellText.text = cell.text
                }
                if (cell.number != 0) { cellView.testindictext.text = cell.number.toString() }
                else { cellView.testindictext.visibility = View.GONE }
            }

            return cellView
        }
    }
}
