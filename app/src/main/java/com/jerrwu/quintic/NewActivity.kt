package com.jerrwu.quintic

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import kotlinx.android.synthetic.main.activity_new.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        setSupportActionBar(toolbar_new_bottom)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm a")
        val curHour: String =  current.format(formatter)
        activity_new_bottom_text.text = curHour

        newBackButton.setOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_new_menu, menu)
        return true
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
