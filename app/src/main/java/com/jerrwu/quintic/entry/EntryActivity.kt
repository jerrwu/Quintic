package com.jerrwu.quintic.entry

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.jerrwu.quintic.R
import com.jerrwu.quintic.helpers.DbHelper
import com.jerrwu.quintic.helpers.InfoHelper
import com.jerrwu.quintic.helpers.StringHelper
import kotlinx.android.synthetic.main.activity_entry.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EntryActivity : AppCompatActivity() {
    companion object {
        private val buttonDisabled = Color.parseColor("#747474")
        private val buttonEnabled = Color.parseColor("#55CF86")
        private const val dbTable = "Cards"
    }

    private var createdDate: LocalDateTime? = null
    private val formatterDate = DateTimeFormatter.ofPattern("E MMM dd, yyyy")
    private val formatterWeekday = DateTimeFormatter.ofPattern("EEEE")
    private val formatterHour = DateTimeFormatter.ofPattern("HH")
    private var dbHelper: DbHelper? = null
    private var mMood: Int? = 0
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        dbHelper = DbHelper(this)

        entryDateTimeView.visibility = View.GONE

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            id = bundle.getInt("ID", 0)
            if (id!=0){
                entryTitleEditText.setText(bundle.getString("Title"))
                entryContentEditText.setText(bundle.getString("Content"))
                createdDate = LocalDateTime.parse(bundle.getString("Time"))
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

        entryContentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length != 0) {
                    entrySaveButton.isEnabled = true

                    if (InfoHelper.isUsingNightMode(resources.configuration)) {
                        entrySaveButton.setColorFilter(buttonEnabled, PorterDuff.Mode.SRC_ATOP)
                    }
                    else {
                        entrySaveButton.setColorFilter(
                            ContextCompat.getColor(applicationContext,
                                R.color.green
                            ),
                            PorterDuff.Mode.SRC_ATOP)
                    }
                }

                else {
                    entrySaveButton.isEnabled = false

                    if (InfoHelper.isUsingNightMode(resources.configuration)) {
                        entrySaveButton.setColorFilter(buttonDisabled, PorterDuff.Mode.SRC_ATOP)
                    }
                    else {
                        entrySaveButton.setColorFilter(
                            ContextCompat.getColor(applicationContext,
                                R.color.colorTertiary
                            ),
                            PorterDuff.Mode.SRC_ATOP)
                    }
                }
            }
        })

    }

    private fun deleteEntry(context: Context) {
        val dbHelper = dbHelper
        if (dbHelper != null) {
            val selectionArgs = arrayOf(id.toString())
            dbHelper.delete("ID=?", selectionArgs)
        }
        finish()
    }

    private fun updateEntry() {
        val dbHelper = dbHelper
        if (dbHelper != null) {
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

            values.put("Title", titleText)
            values.put("Content", conText)
            values.put("DateTime", createdDate.toString())

            if (id == 0) {
                val dbID = dbHelper.insert(values)
                if (dbID > 0) {
                    finish()
                }
            } else {
                val selectionArgs = arrayOf(id.toString())
                val dbID = dbHelper.update(values, "ID=?", selectionArgs)
                if (dbID > 0) {
                    finish()
                }
            }
        }
    }
}
