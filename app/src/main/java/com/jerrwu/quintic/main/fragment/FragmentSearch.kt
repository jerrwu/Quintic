package com.jerrwu.quintic.main.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.mood.adapter.MoodAdapter
import com.jerrwu.quintic.search.adapter.SearchMoodAdapter
import kotlinx.android.synthetic.main.activity_entry.*
import kotlinx.android.synthetic.main.fragment_search.*


class FragmentSearch : Fragment() {
    private var mAdapter: SearchMoodAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moodList = ConstantLists.moodSelectorOptions

        mAdapter = SearchMoodAdapter(moodList)
        moodSearchCarousel.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        moodSearchCarousel.adapter = mAdapter
    }

}
