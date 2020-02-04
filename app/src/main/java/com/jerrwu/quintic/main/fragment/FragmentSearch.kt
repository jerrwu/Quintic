package com.jerrwu.quintic.main.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.time.Month
import com.jerrwu.quintic.search.SearchActivity
import com.jerrwu.quintic.search.adapter.SearchMonthAdapter
import com.jerrwu.quintic.search.adapter.SearchMoodAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import java.time.LocalDate


class FragmentSearch : Fragment() {
    private var mMoodAdapter: SearchMoodAdapter? = null
    private var mMonthAdapter: SearchMonthAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMoodRecycler()
        loadMonthRecycler()
    }

    private fun loadMoodRecycler() {
        val moodList = ConstantLists.searchMoodOptions

        mMoodAdapter = SearchMoodAdapter(moodList)
        moodSearchCarousel.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        moodSearchCarousel.adapter = mMoodAdapter
        moodSearchCarousel.setHasFixedSize(true)

        mMoodAdapter?.onItemClick = { mood ->
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_MOOD)
            intent.putExtra(SearchActivity.SEARCH_STRING, mood.name)
            startActivity(intent)
        }
    }

    private fun loadMonthRecycler() {
        val curMonth = LocalDate.now().monthValue
        val monthList = listOf(
            Month(curMonth - 2),
            Month(curMonth - 1),
            Month(curMonth),
            Month(curMonth + 1),
            Month(curMonth + 2))

        mMonthAdapter = SearchMonthAdapter(monthList)
        mMonthAdapter?.setHasStableIds(true)
        monthSearchRecycler.layoutManager = LinearLayoutManager(activity)
        monthSearchRecycler.adapter = mMonthAdapter
        monthSearchRecycler.isNestedScrollingEnabled = false
        monthSearchRecycler.setHasFixedSize(true)

        mMonthAdapter?.onItemClick = { month ->
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra(SearchActivity.SEARCH_TYPE, SearchActivity.SEARCH_TYPE_TIME)
            intent.putExtra(SearchActivity.SEARCH_STRING, month.toString())
            startActivity(intent)
        }
    }
}
