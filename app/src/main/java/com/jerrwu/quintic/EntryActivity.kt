package com.jerrwu.quintic

import android.content.ContentValues
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_entry.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EntryActivity : AppCompatActivity() {

    private val buttonDisabled = Color.parseColor("#747474")
    private val buttonEnabled = Color.parseColor("#55CF86")
    private val dbTable = "Cards"
    private var createdDate: LocalDateTime? = null
    private val formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy")
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        setSupportActionBar(toolbar_entry_bottom)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        entryDateTimeView.visibility = View.GONE

        try {
            val bundle: Bundle = intent.extras
            id = bundle.getInt("ID", 0)
            if (id!=0){
                entryTitleEditText.setText(bundle.getString("Title"))
                entryContentEditText.setText(bundle.getString("Content"))
                createdDate = LocalDateTime.parse(bundle.getString("Time"))
                val dateString = getString(R.string.created_on) + formatter.format(createdDate)
                entryDateTimeView.text = dateString
                entryDateTimeView.visibility = View.VISIBLE
                entryActivityTopText.text = "Entry"
            }
        }catch (ex:Exception){}

        activity_entry_bottom_text.text = "Bottom Text"

        entryBackButton.setOnClickListener {
            finish()
        }

        entrySaveButton.setOnClickListener {
            addFunc()
        }
        entrySaveButton.isEnabled = false

        entryContentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length != 0) {
                    entrySaveButton.isEnabled = true

                    if (isUsingNightModeResources()) {
                        entrySaveButton.setColorFilter(buttonEnabled, PorterDuff.Mode.SRC_ATOP)
                    }
                    else {
                        entrySaveButton.setColorFilter(
                            ContextCompat.getColor(applicationContext, R.color.green),
                            PorterDuff.Mode.SRC_ATOP)
                    }
                }

                else {
                    entrySaveButton.isEnabled = false

                    if (isUsingNightModeResources()) {
                        entrySaveButton.setColorFilter(buttonDisabled, PorterDuff.Mode.SRC_ATOP)
                    }
                    else {
                        entrySaveButton.setColorFilter(
                            ContextCompat.getColor(applicationContext, R.color.colorTertiary),
                            PorterDuff.Mode.SRC_ATOP)
                    }
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_new_menu, menu)
        return true
    }

    private fun addFunc() {
        val dbManager = DbManager(this)

        val values = ContentValues()
        var titleText = entryTitleEditText.text.toString()
        val conText = entryContentEditText.text.toString()
        if (createdDate == null) {
            createdDate = LocalDateTime.now()
        }
        if (titleText == "") {
            titleText = formatter.format(createdDate)
        }

        values.put("Title", titleText)
        values.put("Content", conText)
        values.put("DateTime", createdDate.toString())

        if (id == 0) {
            val dbID = dbManager.insert(values)
            if (dbID > 0) {
                Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error adding entry...", Toast.LENGTH_SHORT).show()
            }
        } else {
            val selectionArgs = arrayOf(id.toString())
            val dbID = dbManager.update(values, "ID=?", selectionArgs)
            if (dbID > 0) {
                Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error adding entry...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isUsingNightModeResources(): Boolean {
        return when (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }
}
