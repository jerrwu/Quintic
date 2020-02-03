package com.jerrwu.quintic.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jerrwu.quintic.BuildConfig
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.card.CardEntity
import com.jerrwu.quintic.entities.card.adapter.CardAdapter
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.helpers.DbHelper
import com.jerrwu.quintic.helpers.SearchHelper
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {
    private var mSearchResults: List<CardEntity>? = null
    private var mAdapter: CardAdapter? = null
    private var mColumn: String? = null
    private var mDbHelper: DbHelper? = null

    override fun onResume() {
        super.onResume()

        onSearchStarted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchField.requestFocus()
        mDbHelper = DbHelper(this)

        val spinnerList = if (BuildConfig.DEBUG) ConstantLists.searchSpinnerOptionsDebug
        else ConstantLists.searchSpinnerOptions

        val spinnerAdapter = ArrayAdapter(
            this, R.layout.spinner_item, spinnerList
        )

        searchOptionSpinner.adapter = spinnerAdapter
        searchOptionSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val selection = spinnerList[position]
                mColumn = selection
                onSearchStarted()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // no-op
            }
        }

        searchBackButton.setOnClickListener {
            finish()
        }

        searchField.setOnEditorActionListener(OnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchStarted()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun onSearchStarted() {
        val dbHelper = mDbHelper
        if (dbHelper != null)
            mSearchResults = SearchHelper.performSearch(searchField.text.toString(), dbHelper, mColumn)
        val results = mSearchResults
        if (results != null) {
            onSearchPerformed(results)
        }
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
        mAdapter?.mContext = this

        mAdapter?.onItemClick = { card, _ ->
            val intent = Intent(this, EntryActivity::class.java)
            intent.putExtra(DbHelper.DB_COL_ID, card.id)
            intent.putExtra(DbHelper.DB_COL_TITLE, card.title)
            intent.putExtra(DbHelper.DB_COL_CONTENT, card.content)
            intent.putExtra(DbHelper.DB_COL_TIME, card.time.toString())
            intent.putExtra(DbHelper.DB_COL_MOOD, card.mood?.id)
            startActivity(intent)
        }
        return true
    }
}
