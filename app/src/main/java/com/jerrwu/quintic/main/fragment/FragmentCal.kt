package com.jerrwu.quintic.main.fragment


import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
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
import com.jerrwu.quintic.common.view.widget.CalSpinnerAdapter
import com.jerrwu.quintic.entities.cell.CellEntity
import com.jerrwu.quintic.entities.cell.adapter.CellAdapter
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.entry.adapter.DayEntryAdapter
import com.jerrwu.quintic.entities.time.DayEntity
import com.jerrwu.quintic.entities.time.MonthEntity
import com.jerrwu.quintic.entities.time.YearEntity
import com.jerrwu.quintic.helpers.*
import kotlinx.android.synthetic.main.fragment_cal.*
import java.time.LocalDate


class FragmentCal : BaseFragment() {
    companion object {
        val TAG = FragmentCal::class.java.simpleName
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
    private var mIsShowDayView: Boolean = false
    private var mPreviousPosition: Int = 0

    private val mMonthSpinnerList: MutableList<String> = ArrayList()
    private val mYearSpinnerList: MutableList<String> = ArrayList()

    /*
    TODO: improve overall fragment performance, fix bug with indicator not updating
     */

    override fun onFragmentShown() {
        AsyncTask.execute {
            onCalSelected(mCurrentYearValue, mCurrentMonthValue)
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

            AsyncTask.execute {
                if (!onCalSelected(newYear, newMonth))
                    StringHelper.makeSnackbar("Beginning of data!", activity)
            }
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

            AsyncTask.execute {
                if (!onCalSelected(newYear, newMonth))
                    StringHelper.makeSnackbar("End of data!", activity)
            }
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
                mMonthSpinnerList.add(month.stringValue())
            }

            val spinnerAdapter = CalSpinnerAdapter(context, mMonthSpinnerList)

            val monthSpinner = activity?.findViewById<Spinner>(R.id.fragmentCalSelectionSpinner)
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
                SearchHelper.performCalEntryCountSearch(searchDate, it)
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
            val gridView = pActivity.findViewById<GridView>(R.id.calGrid)
            val monthText = pActivity.findViewById<TextView>(R.id.fragmentCalMonthSelectText)
            val yearText = pActivity.findViewById<TextView>(R.id.fragmentCalYearText)
            val monthSpinner = pActivity.findViewById<Spinner>(R.id.fragmentCalSelectionSpinner)

            pActivity.runOnUiThread {
                gridView.adapter = mAdapter
                monthText.text = mCurrentMonth?.stringValue()
                yearText.text = mCurrentYear?.number.toString()

                gridView.onItemClickListener =
                    AdapterView.OnItemClickListener {
                            parent, view, position, id ->
                        if (position == mPreviousPosition) {
                            if (!mIsShowDayView) {
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
        mIsShowDayView = false
    }

    private fun showDayView(cell: CellEntity, context: Context) : Boolean {
        val dayInt = cell.text
        if (dayInt != null && StringHelper.isInteger(dayInt) && Integer.parseInt(dayInt) != 0) {
            val dayString = if (dayInt.length > 1) dayInt else "0$dayInt"
            val query = "${MonthEntity(mCurrentMonthValue).stringValue()} $dayString $mCurrentYearValue"
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
                    entriesRecycler.layoutManager = LinearLayoutManager(activity)
                    entriesRecycler.adapter = entriesAdapter
                    entriesRecycler.isNestedScrollingEnabled = false
                }
            }
            mIsShowDayView = true
            return true
        }
        return false
    }

    private fun fetchDayEntries(date: String, context: Context): List<EntryEntity> {
        val mainDbHelper = MainDbHelper(context)
        return SearchHelper.performSearch(date, mainDbHelper, MainDbHelper.DB_COL_DATE_EXTERNAL)
    }
}
