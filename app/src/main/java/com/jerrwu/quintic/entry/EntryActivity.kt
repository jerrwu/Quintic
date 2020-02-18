package com.jerrwu.quintic.entry

import android.content.ContentValues
import android.content.Context
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

        entry_datetime_text.visibility = View.GONE

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            id = bundle.getInt(MainDbHelper.DB_COL_ID, 0)
            if (id!=0){
                entry_title_edit_text.setText(bundle.getString(MainDbHelper.DB_COL_TITLE))
                entry_context_edittext.setText(bundle.getString(MainDbHelper.DB_COL_CONTENT))
                mCreatedDate = LocalDateTime.parse(bundle.getString(MainDbHelper.DB_COL_TIME))
                mMood = MoodEntity.parse(bundle.getInt(MainDbHelper.DB_COL_MOOD))
                mPos = bundle.getInt("pos")
                val dateString = getString(R.string.created_on) + mFormatterDate.format(mCreatedDate)
                entry_datetime_text.text = dateString
                entry_datetime_text.visibility = View.VISIBLE
                entry_activity_top_text.text = resources.getText(R.string.edit_entry)
            }
        }

        activity_entry_bottom_text.text = "Bottom Text"

        entry_back_button.setOnClickListener {
            finish()
        }

        image_add_button.setOnClickListener {
            Toast.makeText(this, "Not implemented!", Toast.LENGTH_SHORT).show()
        }

        entry_delete_button.setOnClickListener {
            UiUtils.showDialog(
                StringUtils.getString(R.string.confirm_delete_title, this),
                StringUtils.getString(R.string.confirm_delete, this),
                StringUtils.getString(R.string.delete_yes, this),
                StringUtils.getString(R.string.delete_no, this),
                this, this::deleteEntry, UiUtils::dismissDialog)
        }

        entry_save_button.setOnClickListener {
            updateEntry()
        }
        entry_save_button.isEnabled = false

        mood_add_button.setOnClickListener { toggleMoodSelector() }
        mood_add_cancel_button.setOnClickListener { toggleMoodSelector() }

        val moodList = ConstantLists.moodSelectorOptions

        mAdapter = MoodAdapter(moodList, this, mMood)
        mood_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mood_recycler_view.adapter = mAdapter

        mAdapter?.onItemClick = { mood ->
            onMoodUpdated(mood)
        }

        setMoodIcon()

        entry_context_edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                toggleSaveButton(s.toString())
            }
        })

        entry_title_edit_text.addTextChangedListener(object : TextWatcher {
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
            entry_activity_mood_icon.setImageResource(mMood.icFilled)
            entry_activity_mood_icon.setColorFilter(ContextCompat.getColor(applicationContext, mMood.color), PorterDuff.Mode.SRC_ATOP)
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
            mood_selection_container.visibility = View.GONE
            mood_add_button.visibility = View.VISIBLE
            mood_add_cancel_button.visibility = View.GONE
            mIsSelectorOpen = false
        } else {
            mood_selection_container.visibility = View.VISIBLE
            mood_add_button.visibility = View.GONE
            mood_add_cancel_button.visibility = View.VISIBLE
            mIsSelectorOpen = true
        }
    }

    private fun toggleSaveButton() {
        toggleSaveButton("%")
    }

    private fun toggleSaveButton(s: String?) {
        if (s?.length != 0) {
                    entry_save_button.isEnabled = true

                    entry_save_button.setColorFilter(
                        ContextCompat.getColor(this@EntryActivity, R.color.green),
                        PorterDuff.Mode.SRC_ATOP)
                }

                else {
                    entry_save_button.isEnabled = false

                    entry_save_button.setColorFilter(
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
            var titleText = entry_title_edit_text.text.toString()
            val conText = entry_context_edittext.text.toString()

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
