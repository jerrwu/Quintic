package com.jerrwu.quintic.main.fragment


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.account.AccountActivity
import com.jerrwu.quintic.entities.card.CardEntity
import com.jerrwu.quintic.entities.card.adapter.CardAdapter
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.helpers.DbHelper
import com.jerrwu.quintic.helpers.InfoHelper
import com.jerrwu.quintic.helpers.StringHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_entries.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FragmentEntries : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    var mAdapter: RecyclerView.Adapter<*>? = null
    private var cardList: ArrayList<CardEntity> = ArrayList()
    private var mDbHelper: DbHelper? = null

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
        toggleEmptyNotices()

        val mActivity = activity
        if (mActivity != null) {
            mRecyclerView = mActivity.findViewById(R.id.recycler_view)
            val mLayoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
            if (mRecyclerView != null) (mRecyclerView as RecyclerView).layoutManager = mLayoutManager
            mAdapter = CardAdapter(cardList)
            (mAdapter as CardAdapter).useNightMode = InfoHelper.isUsingNightMode(resources.configuration)
        }


        val recyclerView = mRecyclerView
        if (recyclerView != null) {
            registerForContextMenu(recyclerView)
            recyclerView.adapter = mAdapter
            (mAdapter as CardAdapter).onItemLongClick = { card ->
                showSelectionToolbar()
                true
            }
            (mAdapter as CardAdapter).onItemClick = { card, dismissToolbar ->
                // Toast.makeText(activity, card.id.toString(), Toast.LENGTH_SHORT).show()
                if (dismissToolbar) {
                    hideSelectionToolbar()
                } else {
                    val intent = Intent(activity, EntryActivity::class.java)
                    intent.putExtra("ID", card.id)
                    intent.putExtra("Title", card.title)
                    intent.putExtra("Content", card.content)
                    intent.putExtra("Time", card.time.toString())
                    startActivity(intent)
                }
            }
        }
    }

    private fun toggleEmptyNotices() {
        if (cardList.isEmpty()) {
            empty_recycler_notice.visibility = View.VISIBLE
        } else {
            empty_recycler_notice.visibility = View.GONE
            val current = LocalDate.now()
            val filteredCardList: List<CardEntity> = cardList.filter {
                    card -> card.time!!.toLocalDate() == current }
            if (filteredCardList.isEmpty()) {
                daily_suggestion_card_container.visibility = View.VISIBLE
            } else {
                daily_suggestion_card_container.visibility = View.GONE
            }
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
                    val items = (mAdapter as CardAdapter).itemsSelected.toMutableList()
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

    private fun deleteEntries(items: List<CardEntity>) {
        if (mDbHelper == null && activity != null) {
            mDbHelper = DbHelper(activity as Context)
        }
        val dbHelper = mDbHelper
        if (dbHelper != null) {
            for (item in items) {
                val selectionArgs = arrayOf(item.id.toString())
                dbHelper.delete("ID=?", selectionArgs)
            }
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
        if (mDbHelper == null && activity != null) {
            mDbHelper = DbHelper(activity as Context)
        }
        val dbHelper = mDbHelper
        if (dbHelper != null) {
            val projections = arrayOf("ID", "Image", "Title", "Content", "DateTime", "Mood")
            val selectionArgs = arrayOf(title)
            val cursor = dbHelper.query(
                projections, "Title like ?", selectionArgs, "ID"+" DESC")
            cardList.clear()
            if (cursor.moveToFirst()) {

                do {
                    val cdId = cursor.getInt(cursor.getColumnIndex("ID"))
                    val cdIc = cursor.getInt(cursor.getColumnIndex("Image"))
                    val cdTitle = cursor.getString(cursor.getColumnIndex("Title"))
                    val cdCont = cursor.getString(cursor.getColumnIndex("Content"))
                    val cdTime = cursor.getString(cursor.getColumnIndex("DateTime"))
                    val cdMood = cursor.getInt(cursor.getColumnIndex("Mood"))

                    cardList.add(
                        CardEntity(
                            cdId,
                            cdIc,
                            cdTitle,
                            cdCont,
                            LocalDateTime.parse(cdTime),
                            cdMood
                        )
                    )

                } while (cursor.moveToNext())
            }
        }

    }

    private fun resetAdapterSelected() {
        if (mAdapter != null && mAdapter is CardAdapter) {
            (mAdapter as CardAdapter).itemsSelected.clear()
            (mAdapter as CardAdapter).isMultiSelect = false
        }
    }

}
