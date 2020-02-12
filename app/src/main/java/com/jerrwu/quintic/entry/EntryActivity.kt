package com.jerrwu.quintic.entry

import android.content.ContentValues
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.entities.mood.adapter.MoodAdapter
import com.jerrwu.quintic.helpers.InfoHelper
import com.jerrwu.quintic.helpers.MainDbHelper
import com.jerrwu.quintic.helpers.StringHelper
import kotlinx.android.synthetic.main.activity_entry.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EntryActivity : AppCompatActivity() {

    private var createdDate: LocalDateTime? = null
    private val formatterDate = DateTimeFormatter.ofPattern("E MMM dd, yyyy")
    private val formatterWeekday = DateTimeFormatter.ofPattern("EEEE")
    private val formatterHour = DateTimeFormatter.ofPattern("HH")
    private val formatterDb = DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh:mm")
    private var mMainDbHelper: MainDbHelper? = null
    private var mMood: MoodEntity = MoodEntity.NONE
    private var isSelectorOpen = false
    private var mAdapter: MoodAdapter? = null
    var id = 0

    override fun onBackPressed() {
        if (isSelectorOpen) {
            toggleMoodSelector()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        mMainDbHelper = MainDbHelper(this)

        entryDateTimeView.visibility = View.GONE

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            id = bundle.getInt(MainDbHelper.DB_COL_ID, 0)
            if (id!=0){
                entryTitleEditText.setText(bundle.getString(MainDbHelper.DB_COL_TITLE))
                entryContentEditText.setText(bundle.getString(MainDbHelper.DB_COL_CONTENT))
                createdDate = LocalDateTime.parse(bundle.getString(MainDbHelper.DB_COL_TIME))
                mMood = MoodEntity.parse(bundle.getInt(MainDbHelper.DB_COL_MOOD))
                val dateString = getString(R.string.created_on) + formatterDate.format(createdDate)
                entryDateTimeView.text = dateString
                entryDateTimeView.visibility = View.VISIBLE
                entryActivityTopText.text = resources.getText(R.string.edit_entry)
            }
        }

        activityEntryBottomText.text = "Bottom Text"

        entryBackButton.setOnClickListener {
            finish()
        }

        imageAddButton.setOnClickListener {
            Toast.makeText(this, "Not implemented!", Toast.LENGTH_SHORT).show()
        }

        entryDeleteButton.setOnClickListener {
            InfoHelper.showDialog(
                StringHelper.getString(R.string.confirm_delete_title, this),
                StringHelper.getString(R.string.confirm_delete, this),
                StringHelper.getString(R.string.delete_yes, this),
                StringHelper.getString(R.string.delete_no, this),
                this, this::deleteEntry, InfoHelper::dismissDialog)
        }

        entrySaveButton.setOnClickListener {
            updateEntry()
        }
        entrySaveButton.isEnabled = false

        moodAddButton.setOnClickListener { toggleMoodSelector() }
        moodAddCancelButton.setOnClickListener { toggleMoodSelector() }

        val moodList = ConstantLists.moodSelectorOptions

        mAdapter = MoodAdapter(moodList, this, mMood)
        moodRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        moodRecyclerView.adapter = mAdapter

        mAdapter?.onItemClick = { mood ->
            onMoodUpdated(mood)
        }

        setMoodIcon()

        entryContentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                toggleSaveButton(s.toString())
            }
        })

        entryTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                toggleSaveButton(s.toString())
            }

        })

    }

    private fun setMoodIcon() {
        if (mMood != MoodEntity.NONE) {
            entryActivityMoodIcon.setImageResource(mMood.icFilled)
            entryActivityMoodIcon.setColorFilter(ContextCompat.getColor(applicationContext, mMood.color), PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun onMoodUpdated(mood: MoodEntity) {
        mMood = if (mMood != MoodEntity.NONE && mood == mMood) {
            MoodEntity.NONE
        } else {
            mood
        }
        mAdapter?.selected = mMood
        mAdapter?.notifyDataSetChanged()
        toggleSaveButton()
        setMoodIcon()
        onBackPressed()
    }

    private fun toggleMoodSelector() {
        if (isSelectorOpen) {
            moodSelectionContainer.visibility = View.GONE
            moodAddButton.visibility = View.VISIBLE
            moodAddCancelButton.visibility = View.GONE
            isSelectorOpen = false
        } else {
            moodSelectionContainer.visibility = View.VISIBLE
            moodAddButton.visibility = View.GONE
            moodAddCancelButton.visibility = View.VISIBLE
            isSelectorOpen = true
        }
    }

    private fun toggleSaveButton() {
        toggleSaveButton("%")
    }

    private fun toggleSaveButton(s: String?) {
        if (s?.length != 0) {
                    entrySaveButton.isEnabled = true

                    entrySaveButton.setColorFilter(
                        ContextCompat.getColor(this@EntryActivity, R.color.green),
                        PorterDuff.Mode.SRC_ATOP)
                }

                else {
                    entrySaveButton.isEnabled = false

                    entrySaveButton.setColorFilter(
                        ContextCompat.getColor(this@EntryActivity, R.color.colorTertiary),
                        PorterDuff.Mode.SRC_ATOP)
                }
    }

    private fun deleteEntry(context: Context) {
        val dbHelper = mMainDbHelper
        if (dbHelper != null) {
            val selectionArgs = arrayOf(id.toString())
            dbHelper.delete("ID=?", selectionArgs)
        }
        finish()
    }

    private fun updateEntry() {
        val mainDbHelper = mMainDbHelper
        if (mainDbHelper != null) {
            val values = ContentValues()
            var titleText = entryTitleEditText.text.toString()
            val conText = entryContentEditText.text.toString()
            if (createdDate == null) {
                createdDate = LocalDateTime.now()
            }
            if (titleText == "") {
                titleText = formatterWeekday.format(createdDate) + " " +
                        StringHelper.getDaySection(formatterHour.format(createdDate), this)
            }

            values.put(MainDbHelper.DB_COL_TITLE, titleText)
            values.put(MainDbHelper.DB_COL_CONTENT, conText)
            values.put(MainDbHelper.DB_COL_TIME, createdDate.toString())
            Toast.makeText(this, createdDate.toString(), Toast.LENGTH_LONG).show()
            values.put(MainDbHelper.DB_COL_MOOD, mMood.name)
            values.put(MainDbHelper.DB_COL_DATE_EXTERNAL, formatterDb.format(createdDate))
            values.put(MainDbHelper.DB_COL_HOURS, StringHelper.getHours(createdDate?.hour))

            if (id == 0) {
                val dbId = mainDbHelper.insert(values)
                if (dbId != null && dbId > 0) {
                    finish()
                }
            } else {
                val selectionArgs = arrayOf(id.toString())
                val dbId = mainDbHelper.update(values, "ID=?", selectionArgs)
                if (dbId != null && dbId > 0) {
                    finish()
                }
            }
        }
    }
}
