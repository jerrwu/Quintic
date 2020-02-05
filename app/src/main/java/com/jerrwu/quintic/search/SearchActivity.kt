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
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.entry.adapter.EntryAdapter
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.helpers.DbHelper
import com.jerrwu.quintic.helpers.SearchHelper
import kotlinx.android.synthetic.main.activity_search.*
import java.lang.IndexOutOfBoundsException


class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TYPE = "SearchType"
        const val SEARCH_STRING = "SearchString"
        const val EXACT_SEARCH = "ExactSearch"
        const val SEARCH_TYPE_TITLE = "Title"
        const val SEARCH_TYPE_CONTENT = "Content"
        const val SEARCH_TYPE_MOOD = "Mood"
        const val SEARCH_TYPE_TIME = "Time"
        const val SEARCH_TYPE_HOURS = "Hours"
    }

    private var mSearchResults: List<EntryEntity>? = null
    private var mAdapter: EntryAdapter? = null
    private var mColumn: String? = null
    private var mDbHelper: DbHelper? = null
    private var mSearchType: String? = null
    private var mSearchString: String? = null
    private var mUseExact: Boolean = false

    override fun onResume() {
        super.onResume()

        onSearchStarted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mSearchType = bundle.getString(SEARCH_TYPE)
            mSearchString = bundle.getString(SEARCH_STRING)
            mUseExact = bundle.getBoolean(EXACT_SEARCH)
        }

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

        if (mSearchString != null) {
            searchField.setText(mSearchString)
        }

        if (mSearchType != null) try {
            val i = spinnerList.indexOf(mSearchType as String)
            searchOptionSpinner.setSelection(i)
            if (i == 0) onSearchStarted()
        } catch (e: IndexOutOfBoundsException) {
            throw e
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
        val column = mColumn
        if (dbHelper != null && column != null){
            mSearchResults = if (mUseExact) {
                SearchHelper.performExactSearch(
                    searchField.text.toString(), dbHelper, SearchHelper.ORDER_DESCENDING, column)
            } else {
                SearchHelper.performSearch(searchField.text.toString(), dbHelper, column)
            }
        }
        val results = mSearchResults
        if (results != null) {
            onSearchPerformed(results)
        }
    }

    private fun onSearchPerformed(results: List<EntryEntity>): Boolean {
        searchActivityRecyclerView.adapter = null

        if (results.isEmpty()) {
            searchNoResultsText.visibility = View.VISIBLE
            return false
        }

        searchNoResultsText.visibility = View.GONE
        mAdapter = EntryAdapter(results)
        searchActivityRecyclerView.layoutManager = LinearLayoutManager(this)
        searchActivityRecyclerView.adapter = mAdapter
        mAdapter?.mContext = this

        mAdapter?.onItemClick = { entry, _ ->
            val intent = Intent(this, EntryActivity::class.java)
            intent.putExtra(DbHelper.DB_COL_ID, entry.id)
            intent.putExtra(DbHelper.DB_COL_TITLE, entry.title)
            intent.putExtra(DbHelper.DB_COL_CONTENT, entry.content)
            intent.putExtra(DbHelper.DB_COL_TIME, entry.time.toString())
            intent.putExtra(DbHelper.DB_COL_MOOD, entry.mood?.id)
            startActivity(intent)
        }
        return true
    }
}
