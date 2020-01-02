package com.jerrwu.quintic

import android.content.ContentValues
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_entry.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EntryActivity : AppCompatActivity() {

    private val buttonDisabled = Color.parseColor("#747474")
    private val buttonEnabled = Color.parseColor("#A98274")
    val dbTable = "Cards"
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        setSupportActionBar(toolbar_entry_bottom)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        try {
            val bundle:Bundle = intent.extras
            id = bundle.getInt("ID", 0)
            if (id!=0){
                entryTitleEditText.setText(bundle.getString("Title"))
                entryContentEditText.setText(bundle.getString("Content"))
                val createdDate: LocalDateTime? = LocalDateTime.parse(bundle.getString("Time"))
                val formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy")
                entryDateTimeView.text = "Created on " + formatter.format(createdDate)
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
                        entrySaveButton.setColorFilter(R.color.colorAccent, PorterDuff.Mode.SRC_ATOP)
                    }
                }

                else {
                    entrySaveButton.isEnabled = false

                    if (isUsingNightModeResources()) {
                        entrySaveButton.setColorFilter(buttonDisabled, PorterDuff.Mode.SRC_ATOP)
                    }
                    else {
                        entrySaveButton.setColorFilter(R.color.colorTertiary, PorterDuff.Mode.SRC_ATOP)
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
        values.put("Title", entryTitleEditText.text.toString())
        values.put("Content", entryContentEditText.text.toString())

        if (id == 0) {
            val dbID = dbManager.insert(values)
            values.put("DateTime", LocalDateTime.now().toString())
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
