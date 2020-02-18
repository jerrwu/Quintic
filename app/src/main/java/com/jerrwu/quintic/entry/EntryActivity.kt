package com.jerrwu.quintic.entry

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.ConstantLists
import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.entities.mood.adapter.MoodAdapter
import com.jerrwu.quintic.utils.*
import kotlinx.android.synthetic.main.activity_entry.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EntryActivity : AppCompatActivity() {
    companion object {
        val TAG = EntryActivity::class.java.simpleName
    }

    private var mCreatedDate: LocalDateTime? = null
    private val mFormatterDate = DateTimeFormatter.ofPattern("E MMM dd, yyyy")
    private val mFormatterWeekday = DateTimeFormatter.ofPattern("EEEE")
    private val mFormatterHour = DateTimeFormatter.ofPattern("HH")
    private val mFormatterDb = DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh:mm")
    private var mMainDbHelper: MainDbHelper? = null
    private var mCalDbHelper: CalDbHelper? = null
    private var mMood: MoodEntity = MoodEntity.NONE
    private var mIsSelectorOpen = false
    private var mAdapter: MoodAdapter? = null
    private var mPos: Int = 0

    var id = 0

    override fun onBackPressed() {
        if (mIsSelectorOpen) {
            toggleMoodSelector()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        mMainDbHelper = MainDbHelper(this)
        mCalDbHelper = CalDbHelper(this)

        entryDateTimeView.visibility = View.GONE

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            id = bundle.getInt(MainDbHelper.DB_COL_ID, 0)
            if (id!=0){
                entryTitleEditText.setText(bundle.getString(MainDbHelper.DB_COL_TITLE))
                entryContentEditText.setText(bundle.getString(MainDbHelper.DB_COL_CONTENT))
                mCreatedDate = LocalDateTime.parse(bundle.getString(MainDbHelper.DB_COL_TIME))
                mMood = MoodEntity.parse(bundle.getInt(MainDbHelper.DB_COL_MOOD))
                mPos = bundle.getInt("pos")
                val dateString = getString(R.string.created_on) + mFormatterDate.format(mCreatedDate)
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
            UiUtils.showDialog(
                StringUtils.getString(R.string.confirm_delete_title, this),
                StringUtils.getString(R.string.confirm_delete, this),
                StringUtils.getString(R.string.delete_yes, this),
                StringUtils.getString(R.string.delete_no, this),
                this, this::deleteEntry, UiUtils::dismissDialog)
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
        if (mIsSelectorOpen) {
            moodSelectionContainer.visibility = View.GONE
            moodAddButton.visibility = View.VISIBLE
            moodAddCancelButton.visibility = View.GONE
            mIsSelectorOpen = false
        } else {
            moodSelectionContainer.visibility = View.VISIBLE
            moodAddButton.visibility = View.GONE
            moodAddCancelButton.visibility = View.VISIBLE
            mIsSelectorOpen = true
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
        val mainDbHelper = mMainDbHelper
        val calDbHelper = mCalDbHelper
        if (mainDbHelper != null) {
            val selectionArgs = arrayOf(id.toString())
            mainDbHelper.delete("ID=?", selectionArgs)
        }
        if (calDbHelper != null) {
            if (id != 0) {
                val calDbDate = mCreatedDate?.year.toString() +
                        mCreatedDate?.monthValue.toString() +
                        mCreatedDate?.dayOfMonth.toString()

                val result = SearchUtils.performCalEntryCountSearch(calDbDate, calDbHelper)
                val entryCount = result[1]
                val values = ContentValues()

                val calId = result[0]
                values.put(CalDbHelper.DB_COL_DATE, calDbDate.toInt())
                values.put(CalDbHelper.DB_COL_ENTRIES, entryCount - 1)
                val selectionArgs = arrayOf(calId.toString())
                calDbHelper.update(values, "ID=?", selectionArgs)
            }
        }
        finishWithResult("-")
    }

    private fun finishWithResult(type: String) {
//        val resultIntent = Intent()
//        resultIntent.putExtra("notify_type", type)
//        resultIntent.putExtra("entry_id", mPos)
//        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun updateEntry() {
        if (mCreatedDate == null) {
            mCreatedDate = LocalDateTime.now()
        }
        val calDbDate = mCreatedDate?.year.toString() +
                mCreatedDate?.monthValue.toString() +
                mCreatedDate?.dayOfMonth.toString()

        val calDbHelper = mCalDbHelper
        val mainDbHelper = mMainDbHelper

        if (mainDbHelper != null) {
            val values = ContentValues()
            var titleText = entryTitleEditText.text.toString()
            val conText = entryContentEditText.text.toString()

            if (titleText == "") {
                titleText = mFormatterWeekday.format(mCreatedDate) + " " +
                        StringUtils.getDaySection(mFormatterHour.format(mCreatedDate), this)
            }

            values.put(MainDbHelper.DB_COL_TITLE, titleText)
            values.put(MainDbHelper.DB_COL_CONTENT, conText)
            values.put(MainDbHelper.DB_COL_TIME, mCreatedDate.toString())
            values.put(MainDbHelper.DB_COL_MOOD, mMood.name)
            values.put(MainDbHelper.DB_COL_DATE_EXTERNAL, mFormatterDb.format(mCreatedDate))
            Log.d("EntryActivity", "DATE_EXTERNAL: " + values.get(MainDbHelper.DB_COL_DATE_EXTERNAL) as String)
            values.put(MainDbHelper.DB_COL_HOURS, StringUtils.getHours(mCreatedDate?.hour))

            // new entry
            if (id == 0) {
                val dbId = mainDbHelper.insert(values)

                if (calDbHelper != null) {
                    val result = SearchUtils.performCalEntryCountSearch(calDbDate, calDbHelper)
                    val entryCount = result[1]
                    val values = ContentValues()

                    val calId = result[0]
                    values.put(CalDbHelper.DB_COL_DATE, calDbDate.toInt())
                    values.put(CalDbHelper.DB_COL_ENTRIES, entryCount + 1)

                    if (calId == 0) {
                        calDbHelper.insert(values)
                    } else {
                        val selectionArgs = arrayOf(calId.toString())
                        calDbHelper.update(values, "ID=?", selectionArgs)
                    }
                }

                if (dbId != null && dbId > 0) {
                    finishWithResult("!")
                }
            } else {
                val selectionArgs = arrayOf(id.toString())
                val dbId = mainDbHelper.update(values, "ID=?", selectionArgs)
                if (dbId != null && dbId > 0) {
                    finishWithResult("|")
                }
            }
        }
    }
}
