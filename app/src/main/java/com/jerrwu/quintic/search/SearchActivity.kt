package com.jerrwu.quintic.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseActivity
import com.jerrwu.quintic.common.EditTextFlow
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.entry.adapter.EntryAdapter
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.utils.MainDbHelper
import com.jerrwu.quintic.utils.SearchUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit


class SearchActivity : BaseActivity() {
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
        const val SEARCH_TYPE_TAG = "Tags"
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

    override fun onDestroy() {
        mMainDbHelper?.close()
        super.onDestroy()
    }

    @SuppressLint("CheckResult")
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

        search_field.addTextWatcher()
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter { it.type == EditTextFlow.Type.AFTER }
            .filter { it.query.isNotEmpty() }
            .map { it.query }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onSearchStarted(it)
            }
    }

    private fun onSearchStarted() {
        val text = search_field.text.toString()
        onSearchStarted(text)
    }

    private fun onSearchStarted(text: String) {
        val dbHelper = mMainDbHelper
        val column = mColumn
        if (dbHelper != null && column != null){
            mSearchResults = if (mUseExact) {
                SearchUtils.performExactSearch(
                    text, dbHelper, SearchUtils.ORDER_DESCENDING, column)
            } else {
                SearchUtils.performSearch(text, dbHelper, column)
            }
        }
        val results = mSearchResults
        if (results != null) {
            onSearchPerformed(results)
        }
    }

    private fun onSearchPerformed(results: List<EntryEntity>): Boolean {
        val resultsRecycler: RecyclerView = findViewById(R.id.search_activity_recycler_view)

        resultsRecycler.adapter = null

        if (results.isEmpty()) {
            search_no_results_text.visibility = View.VISIBLE
            return false
        }

        search_no_results_text.visibility = View.GONE
        mAdapter = EntryAdapter(results)
        resultsRecycler.layoutManager = LinearLayoutManager(this)
        resultsRecycler.adapter = mAdapter
        mAdapter?.mContext = this

        mAdapter?.onItemClick = { pos, entry, _ ->
            val intent = Intent(this, EntryActivity::class.java)
            intent.putExtra(MainDbHelper.DB_COL_ID, entry.id)
            intent.putExtra(MainDbHelper.DB_COL_TITLE, entry.title)
            intent.putExtra(MainDbHelper.DB_COL_CONTENT, entry.content)
            intent.putExtra(MainDbHelper.DB_COL_TIME, entry.time.toString())
            intent.putExtra(MainDbHelper.DB_COL_MOOD, entry.mood?.id)
            intent.putExtra(MainDbHelper.DB_COL_TAGS, entry.tags)
            intent.putExtra("pos", pos)
            startActivity(intent)
        }
        return true
    }
}
