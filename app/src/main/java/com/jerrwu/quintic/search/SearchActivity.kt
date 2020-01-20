package com.jerrwu.quintic.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jerrwu.quintic.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchField.requestFocus()

        searchBackButton.setOnClickListener {
            finish()
        }
    }
}
