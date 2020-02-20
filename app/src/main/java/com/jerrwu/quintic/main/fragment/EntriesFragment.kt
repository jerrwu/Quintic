package com.jerrwu.quintic.main.fragment


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.account.AccountActivity
import com.jerrwu.quintic.common.BaseFragment
import com.jerrwu.quintic.common.constants.PreferenceKeys
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.entry.adapter.EntryAdapter
import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.utils.*
import com.jerrwu.quintic.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_entries.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EntriesFragment : BaseFragment() {
    companion object {
        val TAG = EntriesFragment::class.java.simpleName
    }

    private var mRecyclerView: RecyclerView? = null
    var mAdapter: EntryAdapter? = null
    private var mEntryList: ArrayList<EntryEntity> = ArrayList()
    private var mMainDbHelper: MainDbHelper? = null
    private var mCalDbHelper: CalDbHelper? = null
    private var mPosToNotify: Int? = null
    private var mPosNotifyType: String? = null

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
        hideSelectionToolbar(false)

        mAdapter?.notifyDataSetChanged()

//        val pos = mPosToNotify
//        val type = mPosNotifyType
//        Log.d(TAG, type ?: "null")
//        if (pos != null && type != null) {
//            when (type) {
//                "-" -> {
//                    mAdapter?.notifyItemRemoved(pos)
//                }
//                "+" -> {
//                    mAdapter?.notifyItemInserted(pos)
//                }
//                "|" -> {
//                    mAdapter?.notifyItemChanged(pos)
//                }
//                else -> {
//                    mAdapter?.notifyDataSetChanged()
//                }
//            }
//            mPosNotifyType = null
//            mPosToNotify = null
//        }
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

        daily_suggestion_card.setOnClickListener {
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
            mAdapter?.onItemClick = {pos, entry, dismissToolbar ->
                if (dismissToolbar) {
                    hideSelectionToolbar(false)
                } else {
                    val intent = Intent(activity, EntryActivity::class.java)
                    intent.putExtra(MainDbHelper.DB_COL_ID, entry.id)
                    intent.putExtra(MainDbHelper.DB_COL_TITLE, entry.title)
                    intent.putExtra(MainDbHelper.DB_COL_CONTENT, entry.content)
                    intent.putExtra(MainDbHelper.DB_COL_TIME, entry.time.toString())
                    intent.putExtra(MainDbHelper.DB_COL_MOOD, entry.mood?.id)
                    intent.putExtra("pos", pos)
                    startActivityForResult(intent, 0)
                }
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
//            mPosNotifyType = data.getStringExtra("notify_type")
//            mPosToNotify = data.getIntExtra("entry_id", 0)
//        }
//    }

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
                val calDbDate = createdDate?.year.toString() +
                        createdDate?.monthValue.toString() +
                        createdDate?.dayOfMonth.toString()

                val result = SearchUtils.performCalEntryCountSearch(calDbDate, calDbHelper)
                val entryCount = result[1]
                val values = ContentValues()

                val calId = result[0]
                values.put(CalDbHelper.DB_COL_DATE, calDbDate.toInt())
                values.put(CalDbHelper.DB_COL_ENTRIES, entryCount - 1)
                var selectionArgs = arrayOf(calId.toString())
                calDbHelper.update(values, "ID=?", selectionArgs)

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
        toggleEmptyNotices()
        loadQuery("%")
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
            mAdapter?.mItemsSelectedIds?.clear()
            mAdapter?.mIsMultiSelect = false
        }
    }

}
