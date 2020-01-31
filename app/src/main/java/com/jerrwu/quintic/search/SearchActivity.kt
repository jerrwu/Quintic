package com.jerrwu.quintic.search

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jerrwu.quintic.R
import com.jerrwu.quintic.entities.card.CardEntity
import com.jerrwu.quintic.entities.card.adapter.CardAdapter
import com.jerrwu.quintic.helpers.DbHelper
import com.jerrwu.quintic.helpers.SearchHelper
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {
    private var mSearchResults: List<CardEntity>? = null
    private var mAdapter: CardAdapter? = null
    private var mDbHelper: DbHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchField.requestFocus()
        mDbHelper = DbHelper(this)

        searchBackButton.setOnClickListener {
            finish()
        }

        searchField.setOnEditorActionListener(OnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val dbHelper = mDbHelper
                if (dbHelper != null)
                mSearchResults = SearchHelper.performSearch(searchField.text.toString(), dbHelper)
                val results = mSearchResults
                if (results != null) {
                    onSearchPerformed(results)
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun onSearchPerformed(results: List<CardEntity>): Boolean {
        searchActivityRecyclerView.adapter = null

        if (results.isEmpty()) {
            searchNoResultsText.visibility = View.VISIBLE
            return false
        }

        searchNoResultsText.visibility = View.GONE
        mAdapter = CardAdapter(results)
        searchActivityRecyclerView.layoutManager = LinearLayoutManager(this)
        searchActivityRecyclerView.adapter = mAdapter
        (mAdapter as CardAdapter).mContext = this
        return true
    }
}
