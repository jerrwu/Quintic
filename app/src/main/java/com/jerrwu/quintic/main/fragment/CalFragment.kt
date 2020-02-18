package com.jerrwu.quintic.main.fragment


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseFragment
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.common.view.DayViewDataWrapper
import com.jerrwu.quintic.common.view.widget.CalSpinnerAdapter
import com.jerrwu.quintic.entities.cell.CellEntity
import com.jerrwu.quintic.entities.cell.adapter.CellAdapter
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.entry.adapter.DayEntryAdapter
import com.jerrwu.quintic.entities.time.DayEntity
import com.jerrwu.quintic.entities.time.MonthEntity
import com.jerrwu.quintic.entities.time.YearEntity
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.utils.*
import kotlinx.android.synthetic.main.fragment_cal.*
import java.time.LocalDate


class CalFragment : BaseFragment() {
    companion object {
        val TAG = CalFragment::class.java.simpleName
    }

    private var mAdapter: CellAdapter? = null
    private var mCellList: MutableList<CellEntity> = ArrayList()
    private var mAddSpacing = true
    private var mWeekDayHeaders: MutableList<CellEntity> = ArrayList()
    private var mYears: List<YearEntity>? = null
    private var mCurrentYear: YearEntity? = null
    private var mCurrentMonth: MonthEntity? = null
    private var mCurrentMonthValue: Int = 0
    private var mCurrentYearValue: Int = 0
    private var mPreviousPosition: Int = 0
    private lateinit var mDayViewData: DayViewDataWrapper

    private val mMonthSpinnerList: MutableList<String> = ArrayList()
    private val mYearSpinnerList: MutableList<String> = ArrayList()

    override fun onFragmentShown() {
        AsyncTask.execute {
            onCalSelected(mCurrentYearValue, mCurrentMonthValue)
            updateDayView()
        }
    }

    private fun updateDayView() {
        val pActivity = activity
        if (!this::mDayViewData.isInitialized) {
            mDayViewData = DayViewDataWrapper(0, mCurrentMonthValue, mCurrentYearValue, false)
        }
        if (mDayViewData.isShow && pActivity != null) {
            Log.d(TAG, "updateDayView() called")
            hideDayView(pActivity)
            if (mDayViewData.currentMonth != mCurrentMonthValue) {
                return
                // TODO: change so if month has same date, show that date instead of just hiding
            }
            showDayView(mCellList[mPreviousPosition], pActivity)
        }
    }

    override fun onFragmentHidden() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set weekdays
        for (header in ConstantLists.calHeaders) {
            mWeekDayHeaders.add(CellEntity(header))
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_cal_month_spinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val monthValue = StringUtils.intOfMonth(mMonthSpinnerList[position])
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

        selector_box.setOnClickListener {
            fragment_cal_month_spinner.performClick()
        }

        fragment_cal_button_backward.setOnClickListener {
            val newYear: Int
            val newMonth: Int

            if (mCurrentMonthValue == 1) {
                newMonth = 12
                newYear = mCurrentYearValue - 1
            } else {
                newMonth = mCurrentMonthValue - 1
                newYear = mCurrentYearValue
            }

            AsyncTask.execute {
                if (!onCalSelected(newYear, newMonth))
                    StringUtils.makeSnackbar("Beginning of data!", activity)
            }
        }

        fragment_cal_button_forward.setOnClickListener {
            val newYear: Int
            val newMonth: Int

            if (mCurrentMonthValue == 12) {
                newMonth = 1
                newYear = mCurrentYearValue + 1
            } else {
                newMonth = mCurrentMonthValue + 1
                newYear = mCurrentYearValue
            }

            AsyncTask.execute {
                if (!onCalSelected(newYear, newMonth))
                    StringUtils.makeSnackbar("End of data!", activity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AsyncTask.execute {
            val now = LocalDate.now()
            mYears = Gson().fromJson<List<YearEntity>>(FileUtils.fromAssetsJson(activity as Context, "cal_test.json"),
                GsonUtils.YearListType)

            if (mCurrentYearValue == 0 || mCurrentMonthValue == 0) {
                onCalSelected(now.year, now.monthValue)
            } else {
                onCalSelected(mCurrentYearValue, mCurrentMonthValue)
            }

            updateDayView()
        }
    }

    private fun setupMonthSpinner() {
        val context = activity
        if (context != null) {
            mMonthSpinnerList.clear()

            for (month in mCurrentYear?.months.orEmpty()) {
                mMonthSpinnerList.add(month.stringValue())
            }

            val spinnerAdapter = CalSpinnerAdapter(context, mMonthSpinnerList)

            val monthSpinner = activity?.findViewById<Spinner>(R.id.fragment_cal_month_spinner)
            activity?.runOnUiThread {
                monthSpinner?.adapter = spinnerAdapter
            }
        }
    }

    private fun onCalSelected(yearValue: Int, monthValue: Int) : Boolean {
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
            return false
        }

        mCellList.addAll(mWeekDayHeaders)

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
                SearchUtils.performCalEntryCountSearch(searchDate, it)
            }
            mCellList.add(CellEntity(day.dayOfMonth.toString(), result?.get(1)))
        }

        mAdapter =
            CellAdapter(
                context,
                mCellList
            )

        val pActivity = activity
        if (pActivity != null) {
            val gridView = pActivity.findViewById<GridView>(R.id.cal_grid)
            val monthText = pActivity.findViewById<TextView>(R.id.fragment_cal_month_select_text)
            val yearText = pActivity.findViewById<TextView>(R.id.fragment_cal_year_text)
            val monthSpinner = pActivity.findViewById<Spinner>(R.id.fragment_cal_month_spinner)

            pActivity.runOnUiThread {
                gridView.adapter = mAdapter
                monthText.text = mCurrentMonth?.stringValue()
                yearText.text = mCurrentYear?.number.toString()

                gridView.onItemClickListener =
                    AdapterView.OnItemClickListener {
                            parent, view, position, id ->
                        if (position == mPreviousPosition) {
                            if (!mDayViewData.isShow) {
                                showDayView(mCellList[position], pActivity)
                            } else {
                                hideDayView(pActivity)
                            }
                        } else {
                            hideDayView(pActivity)
                            showDayView(mCellList[position], pActivity)
                            mPreviousPosition = position
                        }
                    }
            }

            if (yearChanged)
                setupMonthSpinner()
            val index = mMonthSpinnerList.indexOf(mCurrentMonth?.stringValue())
            pActivity.runOnUiThread {
                monthSpinner.setSelection(index)
            }
        }
        return true
    }

    private fun hideDayView(context: Context) {
        context as Activity
        val container: RelativeLayout = context.findViewById(R.id.cal_day_container)
        val entriesRecycler: RecyclerView = context.findViewById(R.id.cal_day_recycler)
        context.runOnUiThread {
            container.visibility = View.GONE
            entriesRecycler.adapter = null
        }
        mDayViewData.hide()
    }

    private fun showDayView(cell: CellEntity, context: Context) : Boolean {
        val dayInt = cell.text
        if (dayInt != null && StringUtils.isInteger(dayInt) && Integer.parseInt(dayInt) != 0) {
            mDayViewData.currentDay = Integer.parseInt(dayInt)
            mDayViewData.currentMonth = mCurrentMonthValue
            mDayViewData.currentYear = mCurrentYearValue

            val dayString = if (dayInt.length > 1) dayInt else "0$dayInt"
            val query = "${MonthEntity(mCurrentMonthValue).stringValue()} $dayString $mCurrentYearValue"
            mDayViewData.currentMonth = mCurrentMonthValue
            val entries = fetchDayEntries(query, context)

            context as Activity
            val container: RelativeLayout = context.findViewById(R.id.cal_day_container)
            val entriesRecycler: RecyclerView = context.findViewById(R.id.cal_day_recycler)
            val noResultsText: TextView = context.findViewById(R.id.cal_no_entries_text)
            val headerText: TextView = context.findViewById(R.id.selected_date_text)

            context.runOnUiThread {
                headerText.text = query
            }

            if (entries.isEmpty()) {
                context.runOnUiThread {
                    container.visibility = View.VISIBLE
                    noResultsText.visibility = View.VISIBLE
                }
            } else {
                val entriesAdapter = DayEntryAdapter(entries, context)
                context.runOnUiThread {
                    container.visibility = View.VISIBLE
                    noResultsText.visibility = View.GONE

                    entriesAdapter.onItemClick = { entry ->
                        val intent = Intent(activity, EntryActivity::class.java)
                        intent.putExtra(MainDbHelper.DB_COL_ID, entry.id)
                        intent.putExtra(MainDbHelper.DB_COL_TITLE, entry.title)
                        intent.putExtra(MainDbHelper.DB_COL_CONTENT, entry.content)
                        intent.putExtra(MainDbHelper.DB_COL_TIME, entry.time.toString())
                        intent.putExtra(MainDbHelper.DB_COL_MOOD, entry.mood?.id)
                        startActivity(intent)
                    }
                    entriesRecycler.layoutManager = LinearLayoutManager(activity)
                    entriesRecycler.adapter = entriesAdapter
                    entriesRecycler.isNestedScrollingEnabled = false
                }
            }
            mDayViewData.show()
            return true
        }
        return false
    }

    private fun fetchDayEntries(date: String, context: Context): List<EntryEntity> {
        val mainDbHelper = MainDbHelper(context)
        return SearchUtils.performSearch(date, mainDbHelper, MainDbHelper.DB_COL_DATE_EXTERNAL)
    }
}
