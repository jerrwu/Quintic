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
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.entry.adapter.EntryAdapter
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.utils.MainDbHelper
import com.jerrwu.quintic.utils.SearchUtils
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {
    companion object {
        val TAG = SearchActivity::class.java.simpleName

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
    private var mMainDbHelper: MainDbHelper? = null
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

        search_field.requestFocus()
        mMainDbHelper = MainDbHelper(this)

        val spinnerList = ConstantLists.searchSpinnerOptions

        val spinnerAdapter = ArrayAdapter(
            this, R.layout.spinner_item, spinnerList
        )

        search_options_spinner.adapter = spinnerAdapter
        search_options_spinner.onItemSelectedListener = object : OnItemSelectedListener {
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
            search_field.setText(mSearchString)
        }

        if (mSearchType != null) try {
            val i = spinnerList.indexOf(mSearchType as String)
            search_options_spinner.setSelection(i)
            if (i == 0) onSearchStarted()
        } catch (e: IndexOutOfBoundsException) {
            throw e
        }

        search_back_button.setOnClickListener {
            finish()
        }

        search_field.setOnEditorActionListener(OnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchStarted()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun onSearchStarted() {
        val dbHelper = mMainDbHelper
        val column = mColumn
        if (dbHelper != null && column != null){
            mSearchResults = if (mUseExact) {
                SearchUtils.performExactSearch(
                    search_field.text.toString(), dbHelper, SearchUtils.ORDER_DESCENDING, column)
            } else {
                SearchUtils.performSearch(search_field.text.toString(), dbHelper, column)
            }
        }
        val results = mSearchResults
        if (results != null) {
            onSearchPerformed(results)
        }
    }

    private fun onSearchPerformed(results: List<EntryEntity>): Boolean {
        search_activity_recycler_view.adapter = null

        if (results.isEmpty()) {
            search_no_results_text.visibility = View.VISIBLE
            return false
        }

        search_no_results_text.visibility = View.GONE
        mAdapter = EntryAdapter(results)
        search_activity_recycler_view.layoutManager = LinearLayoutManager(this)
        search_activity_recycler_view.adapter = mAdapter
        mAdapter?.mContext = this

        mAdapter?.onItemClick = { id, entry, _ ->
            val intent = Intent(this, EntryActivity::class.java)
            intent.putExtra(MainDbHelper.DB_COL_ID, entry.id)
            intent.putExtra(MainDbHelper.DB_COL_TITLE, entry.title)
            intent.putExtra(MainDbHelper.DB_COL_CONTENT, entry.content)
            intent.putExtra(MainDbHelper.DB_COL_TIME, entry.time.toString())
            intent.putExtra(MainDbHelper.DB_COL_MOOD, entry.mood?.id)
            startActivity(intent)
        }
        return true
    }
}
