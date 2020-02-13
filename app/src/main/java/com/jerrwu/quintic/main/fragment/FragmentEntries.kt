package com.jerrwu.quintic.main.fragment


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.account.AccountActivity
import com.jerrwu.quintic.common.BaseFragment
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.entry.adapter.EntryAdapter
import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.helpers.*
import com.jerrwu.quintic.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_entries.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FragmentEntries : BaseFragment() {
    private var mRecyclerView: RecyclerView? = null
    var mAdapter: EntryAdapter? = null
    private var mEntryList: ArrayList<EntryEntity> = ArrayList()
    private var mMainDbHelper: MainDbHelper? = null
    private var mCalDbHelper: CalDbHelper? = null

    override fun onFragmentShown() {
    }

    override fun onFragmentHidden() {
    }

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        setInfoCardGreeting(prefs)
        setInfoCardName(prefs)
        infoCardNameRem(prefs)

        loadQuery("%")
        toggleEmptyNotices()

        resetAdapterSelected()
        hideSelectionToolbar()
        mAdapter?.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_entries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        setInfoCardGreeting(prefs)
        setInfoCardName(prefs)
        infoCardNameRem(prefs)

        nameRemButton.setOnClickListener {
            val intent = Intent(activity, AccountActivity::class.java)
            startActivity(intent)
        }

        nameRemDismissButton.setOnClickListener {
            val editor = prefs.edit()
            editor.putBoolean("setNameRem", false)
            editor.apply()
            infoCardNameRem(prefs)
        }

        dailySuggestionCard.setOnClickListener {
            val intent = Intent(context, EntryActivity::class.java)
            startActivity(intent)
        }

        loadQuery("%")

        val mActivity = activity
        if (mActivity != null) {
            mRecyclerView = mActivity.findViewById(R.id.recycler_view)
            val mLayoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
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
            mAdapter?.onItemClick = { card, dismissToolbar ->
                // Toast.makeText(activity, entry_card.id.toString(), Toast.LENGTH_SHORT).show()
                if (dismissToolbar) {
                    hideSelectionToolbar()
                } else {
                    val intent = Intent(activity, EntryActivity::class.java)
                    intent.putExtra(MainDbHelper.DB_COL_ID, card.id)
                    intent.putExtra(MainDbHelper.DB_COL_TITLE, card.title)
                    intent.putExtra(MainDbHelper.DB_COL_CONTENT, card.content)
                    intent.putExtra(MainDbHelper.DB_COL_TIME, card.time.toString())
                    intent.putExtra(MainDbHelper.DB_COL_MOOD, card.mood?.id)
                    startActivity(intent)
                }
            }
        }
    }

    private fun toggleEmptyNotices() {
        daily_suggestion_card_container.visibility = View.GONE
        if (mEntryList.isEmpty()) {
            empty_recycler_notice.visibility = View.VISIBLE
        } else {
            empty_recycler_notice.visibility = View.GONE
            val current = LocalDate.now()
            val filteredEntryList: List<EntryEntity> = mEntryList.filter {
                    card -> card.time?.toLocalDate() == current }
            if (filteredEntryList.isEmpty()) daily_suggestion_card_container.visibility = View.VISIBLE
        }
    }

    private fun showSelectionToolbar() {
        val mActivity = activity
        if (mActivity != null) {
            mActivity.toolbar_top.visibility = View.GONE
            mActivity.toolbar_multiselect.visibility = View.VISIBLE
            mActivity.toolbarBackButton.setOnClickListener { hideSelectionToolbar() }
            mActivity.toolbarDeleteButton.setOnClickListener {
                if (mAdapter != null) {
                    val items = mAdapter?.mItemsSelected?.toMutableList()
                    if (items != null)
                    InfoHelper.showDialog(
                        StringHelper.getString(R.string.confirm_delete_multiple_title, mActivity),
                        StringHelper.getString(R.string.confirm_delete_multiple, mActivity),
                        StringHelper.getString(R.string.delete_yes, mActivity),
                        StringHelper.getString(R.string.delete_no, mActivity),
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
                val calDbDate = createdDate?.year.toString() +
                        createdDate?.monthValue.toString() +
                        createdDate?.dayOfMonth.toString()

                val result = SearchHelper.performCalEntryCountSearch(calDbDate, calDbHelper)
                val entryCount = result[1]
                val values = ContentValues()

                val calId = result[0]
                values.put(CalDbHelper.DB_COL_DATE, calDbDate.toInt())
                values.put(CalDbHelper.DB_COL_ENTRIES, entryCount - 1)
                var selectionArgs = arrayOf(calId.toString())
                calDbHelper.update(values, "ID=?", selectionArgs)

                selectionArgs = arrayOf(item.id.toString())
                mainDbHelper.delete("ID=?", selectionArgs)
            }
        }
        if (activity is MainActivity) {
            (activity as MainActivity).mRefreshCalFragmentGrid = true
        }
        hideSelectionToolbar()
        toggleEmptyNotices()
    }

    fun hideSelectionToolbar() {
        val activity = activity
        if (activity != null) {
            activity.toolbar_top.visibility = View.VISIBLE
            activity.toolbar_multiselect.visibility = View.GONE
        }
        loadQuery("%")
        resetAdapterSelected()
        mAdapter?.notifyDataSetChanged()
    }

    private fun setInfoCardGreeting(prefs: SharedPreferences) {
        val greetingString: String?
        val greetingsToggle = prefs.getBoolean("greetings", true)
        if (greetingsToggle) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH")
            val curHour: String = current.format(formatter)
            greetingString = StringHelper.getGreeting(curHour, context)
            fragmentEntriesInfoCard.visibility = View.VISIBLE
        } else {
            greetingString = ""
            fragmentEntriesInfoCard.visibility = View.GONE
        }
        infoCardGreeting.text = greetingString.replace("!", ",")
    }

    private fun setInfoCardName(prefs: SharedPreferences) {
        val nameString: String? = prefs.getString("name", "user")
        infoCardName.text = nameString
    }

    private fun infoCardNameRem(prefs: SharedPreferences) {
        val remBool: Boolean = prefs.getBoolean("setNameRem", false)
        if (remBool) {
            infoCardGreetingType.visibility = View.GONE
            infoCardRemType.visibility = View.VISIBLE
            infoCardGreetingIcon.visibility = View.GONE
            infoCardReminderIcon.visibility = View.VISIBLE
        }
        else {
            infoCardGreetingType.visibility = View.VISIBLE
            infoCardRemType.visibility = View.GONE
            infoCardGreetingIcon.visibility = View.VISIBLE
            infoCardReminderIcon.visibility = View.GONE
        }
    }

    private fun loadQuery(title: String) {
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
                MainDbHelper.DB_COL_MOOD)
            val selectionArgs = arrayOf(title)
            val cursor = dbHelper.query(
                projections, "Title like ?", selectionArgs, MainDbHelper.DB_COL_ID+" DESC")
            mEntryList.clear()
            if (cursor.moveToFirst()) {

                do {
                    val cdId = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ID))
                    val cdIc = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ICON))
                    val cdTitle = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TITLE))
                    val cdCont = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_CONTENT))
                    val cdTime = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TIME))
                    val cdMood = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_MOOD))

                    mEntryList.add(
                        EntryEntity(
                            cdId,
                            cdIc,
                            cdTitle,
                            cdCont,
                            LocalDateTime.parse(cdTime),
                            MoodEntity.parse(cdMood)
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
            mAdapter?.mIsMultiSelect = false
        }
    }

}
