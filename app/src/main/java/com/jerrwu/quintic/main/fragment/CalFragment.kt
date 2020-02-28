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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.base.BaseFragment
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
import com.jerrwu.quintic.main.viewmodel.CalViewModel
import com.jerrwu.quintic.utils.*
import kotlinx.android.synthetic.main.fragment_cal.*
import java.time.LocalDate


class CalFragment : BaseFragment() {
    companion object {
        val TAG = CalFragment::class.java.simpleName
    }

    override lateinit var mViewModel: CalViewModel

    private var mAdapter: CellAdapter? = null
    private lateinit var mDayViewData: DayViewDataWrapper

    private val mMonthSpinnerList: MutableList<String> = ArrayList()
    private val mYearSpinnerList: MutableList<String> = ArrayList()

    override fun onFragmentShown() {
        val currentMonthValue = mViewModel.currentMonthValue
        val currentYearValue = mViewModel.currentYearValue
        onCalSelected(currentYearValue, currentMonthValue)
        updateDayView()
    }

    private fun updateDayView() {
        val pActivity = activity
        val currentMonthValue = mViewModel.currentMonthValue
        val currentYearValue = mViewModel.currentYearValue
        if (!this::mDayViewData.isInitialized) {
            mDayViewData = DayViewDataWrapper(0, currentMonthValue, currentYearValue, false)
        }
        if (mDayViewData.isShow && pActivity != null) {
            Log.d(TAG, "updateDayView() called")
            hideDayView(pActivity)
            if (mDayViewData.currentMonth != currentMonthValue) {
                return
                // TODO: change so if month has same date, show that date instead of just hiding
            }
            mViewModel.getCellList(context).observe(viewLifecycleOwner, Observer { cellList ->
                showDayView(cellList[mViewModel.previousPosition], pActivity)
            })
        }
    }

    override fun onFragmentHidden() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(CalViewModel::class.java)
        return inflater.inflate(R.layout.fragment_cal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentMonthValue = mViewModel.currentMonthValue
        val currentYearValue = mViewModel.currentYearValue

        fragment_cal_month_spinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val monthValue = StringUtils.intOfMonth(mMonthSpinnerList[position])
                if (monthValue != currentMonthValue){
                    onCalSelected(currentYearValue, monthValue)
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

            if (mViewModel.currentMonthValue == 1) {
                newMonth = 12
                newYear = currentYearValue - 1
            } else {
                newMonth = currentMonthValue - 1
                newYear = currentYearValue
            }

            if (!onCalSelected(newYear, newMonth))
                StringUtils.makeSnackbar("Beginning of data!", activity)
        }

        fragment_cal_button_forward.setOnClickListener {
            val newYear: Int
            val newMonth: Int

            if (currentMonthValue == 12) {
                newMonth = 1
                newYear = currentYearValue + 1
            } else {
                newMonth = currentMonthValue + 1
                newYear = currentYearValue
            }

            if (!onCalSelected(newYear, newMonth))
                StringUtils.makeSnackbar("End of data!", activity)
        }
    }

    override fun onResume() {
        super.onResume()
        val now = LocalDate.now()

        val currentMonthValue = mViewModel.currentMonthValue
        val currentYearValue = mViewModel.currentYearValue

        if (currentMonthValue == 0 || currentYearValue == 0) {
            onCalSelected(now.year, now.monthValue)
        } else {
            onCalSelected(currentYearValue, currentMonthValue)
        }

        updateDayView()
    }

    private fun setupMonthSpinner() {
        val context = activity
        if (context != null) {
            mMonthSpinnerList.clear()

            mViewModel.currentYear.observe(viewLifecycleOwner, Observer { currentYear ->
                for (month in currentYear?.months.orEmpty()) {
                    mMonthSpinnerList.add(month.stringValue())
                }

                val spinnerAdapter = CalSpinnerAdapter(context, mMonthSpinnerList)

                val monthSpinner = activity?.findViewById<Spinner>(R.id.fragment_cal_month_spinner)
                monthSpinner?.adapter = spinnerAdapter
            })

        }
    }

    private fun onCalSelected(yearValue: Int, monthValue: Int) : Boolean {
        // Set current year and month
        var selectedYearExists = false
        var selectedMonthExists = false

        val currentMonthValue = mViewModel.currentMonthValue
        val currentYearValue = mViewModel.currentYearValue

        var yearChanged = false
        if (currentYearValue != yearValue) {
            yearChanged = true
        }

        mViewModel.getYears(context).observe(viewLifecycleOwner, Observer { years ->
            for (year: YearEntity in years.orEmpty()) {
                if (yearValue == year.number) {
                    mViewModel.setCurrentYear(year)
                    selectedYearExists = true
                    break
                }
            }
        })

        mViewModel.currentYear.observe(viewLifecycleOwner, Observer { currentYear ->
            for (month: MonthEntity in currentYear.months.orEmpty()) {
                if (monthValue == month.number) {
                    mViewModel.setCurrentMonth(month)
                    selectedMonthExists = true
                    break
                }
            }
        })

        if (!selectedYearExists || !selectedMonthExists) {
            return false
        }

        mViewModel.getCellList(context).observe(viewLifecycleOwner, Observer { cellList ->
            mAdapter =
                CellAdapter(
                    context,
                    cellList
                )

            val pActivity = activity
            if (pActivity != null) {
                val gridView = pActivity.findViewById<GridView>(R.id.cal_grid)
                val monthText = pActivity.findViewById<TextView>(R.id.fragment_cal_month_select_text)
                val yearText = pActivity.findViewById<TextView>(R.id.fragment_cal_year_text)
                val monthSpinner = pActivity.findViewById<Spinner>(R.id.fragment_cal_month_spinner)

                gridView.adapter = mAdapter

                gridView.onItemClickListener =
                    AdapterView.OnItemClickListener {
                            parent, view, position, id ->
                        if (position == mViewModel.previousPosition) {
                            if (!mDayViewData.isShow) {
                                showDayView(cellList[position], pActivity)
                            } else {
                                hideDayView(pActivity)
                            }
                        } else {
                            hideDayView(pActivity)
                            showDayView(cellList[position], pActivity)
                            mViewModel.previousPosition = position
                        }
                    }

                if (yearChanged)
                    setupMonthSpinner()

                mViewModel.currentMonth.observe(viewLifecycleOwner, Observer { currentYear ->
                    monthText.text = currentYear.stringValue()
                })

                mViewModel.currentMonth.observe(viewLifecycleOwner, Observer { currentMonth ->
                    yearText.text = currentMonth.number.toString()
                    val index = mMonthSpinnerList.indexOf(currentMonth?.stringValue())
                    monthSpinner.setSelection(index)
                })
            }
        })
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
        val currentMonthValue = mViewModel.currentMonthValue
        val currentYearValue = mViewModel.currentYearValue

        if (dayInt != null && StringUtils.isInteger(dayInt) && Integer.parseInt(dayInt) != 0) {
            mDayViewData.currentDay = Integer.parseInt(dayInt)
            mDayViewData.currentMonth = currentMonthValue
            mDayViewData.currentYear = currentYearValue

            val dayString = if (dayInt.length > 1) dayInt else "0$dayInt"
            val query = "${MonthEntity(currentMonthValue).stringValue()} $dayString $currentYearValue"
            mDayViewData.currentMonth = currentMonthValue
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

                    entriesAdapter.onItemClick = {_, entry, _ ->
                        val intent = Intent(activity, EntryActivity::class.java)
                        intent.putExtra(MainDbHelper.DB_COL_ID, entry.id)
                        intent.putExtra(MainDbHelper.DB_COL_TITLE, entry.title)
                        intent.putExtra(MainDbHelper.DB_COL_CONTENT, entry.content)
                        intent.putExtra(MainDbHelper.DB_COL_TIME, entry.time.toString())
                        intent.putExtra(MainDbHelper.DB_COL_MOOD, entry.mood?.id)
                        intent.putExtra(MainDbHelper.DB_COL_TAGS, entry.tags)
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
        val results = SearchUtils.performSearch(date, mainDbHelper, MainDbHelper.DB_COL_DATE_EXTERNAL)
        mainDbHelper.close()
        return results
    }
}
