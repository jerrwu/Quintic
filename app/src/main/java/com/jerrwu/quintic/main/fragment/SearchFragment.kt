package com.jerrwu.quintic.main.fragment


import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.base.BaseFragment
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.time.MonthEntity
import com.jerrwu.quintic.main.viewmodel.SearchViewModel
import com.jerrwu.quintic.search.SearchActivity
import com.jerrwu.quintic.search.adapter.SearchHoursAdapter
import com.jerrwu.quintic.search.adapter.SearchMonthAdapter
import com.jerrwu.quintic.search.adapter.SearchMoodAdapter
import java.time.LocalDate


class SearchFragment : BaseFragment() {
    companion object {
        val TAG = SearchFragment::class.java.simpleName
    }

    override lateinit var mViewModel: SearchViewModel

    private var mMoodAdapter: SearchMoodAdapter? = null
    private var mHoursAdapter: SearchHoursAdapter? = null
    private var mMonthAdapter: SearchMonthAdapter? = null

    override fun onFragmentShown() {
    }

    override fun onFragmentHidden() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AsyncTask.execute {
            loadMoodRecycler()
            loadHoursRecycler()
            loadMonthRecycler()
        }
    }

    private fun loadMoodRecycler() {
        val moodList = ConstantLists.searchMoodOptions
        val pContext = context

        if (pContext != null) {
            mMoodAdapter = SearchMoodAdapter(moodList, pContext)
            val recycler = (pContext as Activity).findViewById<RecyclerView>(R.id.mood_search_carousel)
            pContext.runOnUiThread {
                recycler.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recycler.adapter = mMoodAdapter
                recycler.setHasFixedSize(true)
            }

            mMoodAdapter?.onItemClick = {_, mood, _ ->
                val intent = Intent(activity, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_MOOD)
                intent.putExtra(SearchActivity.SEARCH_STRING, mood.name)
                intent.putExtra(SearchActivity.EXACT_SEARCH, true)
                startActivity(intent)
            }
        }
    }

    private fun loadHoursRecycler() {
        val hoursList = ConstantLists.searchHoursOptions
        val pContext = context

        if (pContext != null) {
            mHoursAdapter = SearchHoursAdapter(hoursList, pContext)
            val recycler = (pContext as Activity).findViewById<RecyclerView>(R.id.hours_search_carousel)
            pContext.runOnUiThread {
                recycler.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recycler.adapter = mHoursAdapter
                recycler.setHasFixedSize(true)
            }

            mHoursAdapter?.onItemClick = {_, name, _ ->
                val intent = Intent(activity, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_HOURS)
                intent.putExtra(SearchActivity.SEARCH_STRING, name)
                intent.putExtra(SearchActivity.EXACT_SEARCH, true)
                startActivity(intent)
            }
        }
    }

    private fun loadMonthRecycler() {
        val curMonth = LocalDate.now().monthValue
        val monthList = listOf(
            MonthEntity(curMonth - 2),
            MonthEntity(curMonth - 1),
            MonthEntity(curMonth),
            MonthEntity(curMonth + 1),
            MonthEntity(curMonth + 2))
        val pContext = context

        if (pContext != null) {
            mMonthAdapter = SearchMonthAdapter(monthList, pContext)
            mMonthAdapter?.setHasStableIds(true)
            val recycler = (pContext as Activity).findViewById<RecyclerView>(R.id.month_search_recycler)
            pContext.runOnUiThread {
                recycler.layoutManager = LinearLayoutManager(activity)
                recycler.adapter = mMonthAdapter
                recycler.isNestedScrollingEnabled = false
                recycler.setHasFixedSize(true)
            }

            mMonthAdapter?.onItemClick = {_, month, _ ->
                val intent = Intent(activity, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_TIME)
                intent.putExtra(SearchActivity.SEARCH_STRING, month.stringValue())
                intent.putExtra(SearchActivity.EXACT_SEARCH, false)
                startActivity(intent)
            }
        }
    }
}
