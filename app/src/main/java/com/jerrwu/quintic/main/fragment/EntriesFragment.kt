package com.jerrwu.quintic.main.fragment


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jerrwu.quintic.R
import com.jerrwu.quintic.account.AccountActivity
import com.jerrwu.quintic.common.base.BaseFragment
import com.jerrwu.quintic.common.constants.PreferenceKeys
import com.jerrwu.quintic.common.view.NoPredictiveAnimationLinearLayoutManager
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.entry.adapter.EntryAdapter
import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.main.MainActivity
import com.jerrwu.quintic.main.viewmodel.EntriesViewModel
import com.jerrwu.quintic.utils.*
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_entries.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EntriesFragment : BaseFragment() {
    companion object {
        val TAG = EntriesFragment::class.java.simpleName
    }

    override lateinit var mViewModel: EntriesViewModel

    private var mRecyclerView: RecyclerView? = null
    var mAdapter: EntryAdapter? = null
    private var mEntryList: ArrayList<EntryEntity> = ArrayList()
    private var mMainDbHelper: MainDbHelper? = null
    private var mCalDbHelper: CalDbHelper? = null

    override fun onFragmentShown() {
    }

    override fun onFragmentHidden() {
    }

    override fun onDestroy() {
        mMainDbHelper?.close()
        mCalDbHelper?.close()
        super.onDestroy()
    }

    private fun doRefresh() {
        Completable.fromAction {
            Log.d(TAG, "Refresh started.")

            loadQuery("%")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                toggleEmptyNotices()
                val refreshLayout: SwipeRefreshLayout? = activity?.findViewById(R.id.fragment_entries_pull_refresh)
                refreshLayout?.isRefreshing = false

                Log.d(TAG, "Refresh finished.")
            }
            .doOnError {
                activity?.runOnUiThread {
                    Toast.makeText(activity, R.string.refresh_error, Toast.LENGTH_SHORT).show()
                }

                Log.d(TAG, "Refresh failed.")
            }
            .subscribe()
    }

    private fun doFullRefresh() {
        Completable.fromAction {
            Log.d(TAG, "Refresh started.")

            loadQuery("%")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                toggleEmptyNotices()
                val refreshLayout: SwipeRefreshLayout? = activity?.findViewById(R.id.fragment_entries_pull_refresh)
                refreshLayout?.isRefreshing = false

                mRecyclerView?.recycledViewPool?.clear()
                mAdapter?.notifyDataSetChanged()

                Log.d(TAG, "Refresh finished.")
            }
            .doOnError {
                Log.d(TAG, "Refresh failed.")
            }
            .subscribe()
    }

    override fun onResume() {
        super.onResume()

        mViewModel.getPreferences(activity).observe(viewLifecycleOwner, Observer {prefs ->
            setInfoCardGreeting(prefs)
            setInfoCardName(prefs)
            infoCardNameRem(prefs)

            name_rem_button.setOnClickListener {
                val intent = Intent(activity, AccountActivity::class.java)
                startActivity(intent)
            }

            name_rem_dismiss_button.setOnClickListener {
                val editor = prefs.edit()
                editor.putBoolean(PreferenceKeys.PREFERENCE_SET_NAME_REMINDER, false)
                editor.apply()
                infoCardNameRem(prefs)
            }
        })

        doFullRefresh()

        resetAdapterSelected()
        hideSelectionToolbar(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(EntriesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_entries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daily_suggestion_card.setOnClickListener {
            val intent = Intent(context, EntryActivity::class.java)
            startActivity(intent)
        }

        doRefresh()

        fragment_entries_pull_refresh.setColorSchemeResources(
            R.color.colorAccent,
            R.color.purple,
            R.color.green,
            R.color.blue,
            R.color.yellow,
            R.color.red)
        fragment_entries_pull_refresh.setProgressBackgroundColorSchemeResource(R.color.colorMain)
        fragment_entries_pull_refresh.setOnRefreshListener {
            doRefresh()
        }

        val mActivity = activity
        if (mActivity != null) {
            mRecyclerView = mActivity.findViewById(R.id.recycler_view)
            val mLayoutManager = NoPredictiveAnimationLinearLayoutManager(
                mActivity, LinearLayoutManager.VERTICAL, false)
            if (mRecyclerView != null) mRecyclerView?.layoutManager = mLayoutManager
            mAdapter = EntryAdapter(mEntryList)
            mAdapter?.mContext = mActivity
        }

        val recyclerView = mRecyclerView
        if (recyclerView != null) {
            recyclerView.adapter = mAdapter
            mAdapter?.onItemLongClick = { _ ->
                showSelectionToolbar()
                true
            }
            mAdapter?.onItemClick = {pos, entry, dismissToolbar ->
                if (dismissToolbar!!) {
                    hideSelectionToolbar(false)
                } else {
                    val intent = Intent(activity, EntryActivity::class.java)
                    intent.putExtra(MainDbHelper.DB_COL_ID, entry.id)
                    intent.putExtra(MainDbHelper.DB_COL_TITLE, entry.title)
                    intent.putExtra(MainDbHelper.DB_COL_CONTENT, entry.content)
                    intent.putExtra(MainDbHelper.DB_COL_TIME, entry.time.toString())
                    intent.putExtra(MainDbHelper.DB_COL_MOOD, entry.mood?.id)
                    intent.putExtra(MainDbHelper.DB_COL_TAGS, entry.tags)
                    intent.putExtra("pos", pos)
                    startActivityForResult(intent, 0)
                }
            }
        }
    }

    @UiThread
    private fun toggleEmptyNotices() {
        val dailyNotice: CardView? = activity?.findViewById(R.id.daily_suggestion_card_container)
        val emptyNotice: LinearLayout? = activity?.findViewById(R.id.empty_recycler_notice)

        dailyNotice?.visibility = View.GONE
        if (mEntryList.isEmpty() && mAdapter?.itemCount == 0) {
            emptyNotice?.visibility = View.VISIBLE
        } else {
            emptyNotice?.visibility = View.GONE
            val current = LocalDate.now()
            val filteredEntryList: List<EntryEntity> = mEntryList.filter {
                    card -> card.time.toLocalDate() == current }
            if (filteredEntryList.isEmpty()) dailyNotice?.visibility = View.VISIBLE
        }
    }

    private fun showSelectionToolbar() {
        val mActivity = activity
        if (mActivity != null) {
            mActivity.toolbar_top.visibility = View.GONE
            mActivity.toolbar_multiselect.visibility = View.VISIBLE
            mActivity.toolbar_back_button.setOnClickListener { hideSelectionToolbar(false) }
            mActivity.toolbar_delete_button.setOnClickListener {
                if (mAdapter != null) {
                    val items = mAdapter?.mItemsSelected?.toMutableList()
                    if (items != null)
                    UiUtils.showDialog(
                        StringUtils.getString(R.string.confirm_delete_multiple_title, mActivity),
                        StringUtils.getString(R.string.confirm_delete_multiple, mActivity),
                        StringUtils.getString(R.string.delete_yes, mActivity),
                        StringUtils.getString(R.string.delete_no, mActivity),
                        mActivity, this::deleteEntries, items
                    )
                }
            }
        }
    }

    private fun deleteEntries(items: List<EntryEntity>) {
        if (mMainDbHelper == null && activity != null) {
            mMainDbHelper = MainDbHelper(activity as Context)
        }
        if (mCalDbHelper == null && activity != null) {
            mCalDbHelper = CalDbHelper(activity as Context)
        }
        val mainDbHelper = mMainDbHelper
        val calDbHelper = mCalDbHelper
        if (mainDbHelper != null && calDbHelper != null) {
            for (item in items) {
                val createdDate = item.time
                val calDbDate = createdDate.year.toString() +
                        createdDate.monthValue.toString() +
                        createdDate.dayOfMonth.toString()

                val result = SearchUtils.performCalEntryCountSearch(calDbDate, calDbHelper)
                val entryCount = result[1]
                val columnValues = ContentValues()

                val calId = result[0]
                columnValues.put(CalDbHelper.DB_COL_DATE, calDbDate.toInt())
                columnValues.put(CalDbHelper.DB_COL_ENTRIES, entryCount - 1)
                var selectionArgs = arrayOf(calId.toString())
                calDbHelper.update(columnValues, "ID=?", selectionArgs)

                selectionArgs = arrayOf(item.id.toString())
                mainDbHelper.delete("ID=?", selectionArgs)

                for (id in mAdapter?.mItemsSelectedIds.orEmpty()) {
                    mAdapter?.notifyItemRemoved(id)
                }
            }
        }
        if (activity is MainActivity) {
            (activity as MainActivity).mRefreshCalFragmentGrid = true
        }
        hideSelectionToolbar(true)
        doRefresh()
    }

    fun hideSelectionToolbar(deleted: Boolean) {
        val activity = activity
        if (activity != null) {
            activity.toolbar_top.visibility = View.VISIBLE
            activity.toolbar_multiselect.visibility = View.GONE
        }
        if (!deleted) {
            for ((index, id) in mAdapter?.mItemsSelectedIds.orEmpty().withIndex()) {
                val item = mAdapter?.mItemsSelected?.get(index)
                item?.isSelected = false
                mAdapter?.notifyItemChanged(id, item)
            }
        }
        resetAdapterSelected()
    }

    private fun setInfoCardGreeting(prefs: SharedPreferences) {
        val greetingString: String?
        val greetingsToggle = prefs.getBoolean(PreferenceKeys.PREFERENCE_GREETINGS, true)
        if (greetingsToggle) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH")
            val curHour: String = current.format(formatter)
            greetingString = StringUtils.getGreeting(curHour, context)
            fragmen_entries_info_card.visibility = View.VISIBLE
        } else {
            greetingString = ""
            fragmen_entries_info_card.visibility = View.GONE
        }
        info_card_greeting.text = greetingString.replace("!", ",")
    }

    private fun setInfoCardName(prefs: SharedPreferences) {
        val nameString: String? = prefs.getString(PreferenceKeys.PREFERENCE_NAME, "user")
        info_card_name.text = nameString
    }

    private fun infoCardNameRem(prefs: SharedPreferences) {
        val remBool: Boolean = prefs.getBoolean(PreferenceKeys.PREFERENCE_SET_NAME_REMINDER, false)
        if (remBool) {
            info_card_greeting_type.visibility = View.GONE
            info_card_rem_type.visibility = View.VISIBLE
            info_card_greeting_icon.visibility = View.GONE
            info_card_reminder_icon.visibility = View.VISIBLE
        }
        else {
            info_card_greeting_type.visibility = View.VISIBLE
            info_card_rem_type.visibility = View.GONE
            info_card_greeting_icon.visibility = View.VISIBLE
            info_card_reminder_icon.visibility = View.GONE
        }
    }

    @WorkerThread
    private fun loadQuery(title: String) {
        val refreshLayout: SwipeRefreshLayout? = activity?.findViewById(R.id.fragment_entries_pull_refresh)

        activity?.runOnUiThread {
            if (refreshLayout != null && !refreshLayout.isRefreshing) {
                refreshLayout.isRefreshing = true
            }
        }

        if (mMainDbHelper == null && activity != null) {
            mMainDbHelper = MainDbHelper(activity as Context)
        }
        val dbHelper = mMainDbHelper
        if (dbHelper != null) {
            val projections = arrayOf(
                MainDbHelper.DB_COL_ID,
                MainDbHelper.DB_COL_ICON,
                MainDbHelper.DB_COL_TITLE,
                MainDbHelper.DB_COL_CONTENT,
                MainDbHelper.DB_COL_TIME,
                MainDbHelper.DB_COL_MOOD,
                MainDbHelper.DB_COL_TAGS)
            val selectionArgs = arrayOf(title)
            val cursor = dbHelper.query(
                projections, "Title like ?", selectionArgs, MainDbHelper.DB_COL_ID+" DESC")
            mEntryList.clear()
            if (cursor.moveToFirst()) {

                do {
                    val entryId = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ID))
                    val entryIc = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ICON))
                    val entryTitle = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TITLE))
                    val entryContent = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_CONTENT))
                    val entryTime = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TIME))
                    val entryMood = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_MOOD))
                    val entryTags = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TAGS))

                    mEntryList.add(
                        EntryEntity(
                            entryId,
                            entryIc,
                            entryTitle,
                            entryContent,
                            LocalDateTime.parse(entryTime),
                            MoodEntity.parse(entryMood),
                            entryTags
                        )
                    )

                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    private fun resetAdapterSelected() {
        if (mAdapter != null && mAdapter is EntryAdapter) {
            mAdapter?.mItemsSelected?.clear()
            mAdapter?.mItemsSelectedIds?.clear()
            mAdapter?.mIsMultiSelect = false
        }
    }

}
