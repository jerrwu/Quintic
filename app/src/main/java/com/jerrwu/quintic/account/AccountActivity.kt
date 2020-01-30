package com.jerrwu.quintic.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_account.*
import android.text.Editable
import android.text.TextWatcher
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import com.jerrwu.quintic.R


class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        accountBackButton.setOnClickListener {
            finish()
        }

        accountSaveButton.setOnClickListener {
            val editor = prefs.edit()
            editor.putString("name", accountEditText.text.toString())
            editor.putBoolean("setNameRem", false)
            editor.apply()
            finish()
        }
        accountSaveButton.isClickable = false

        accountEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length != 0) {
                    accountSaveButton.isClickable = true

                    accountSaveButton.backgroundTintList =
                        ContextCompat.getColorStateList(this@AccountActivity,
                            R.color.colorAccent
                        )
                    account_check_image.setColorFilter(
                        ContextCompat.getColor(this@AccountActivity,
                            R.color.colorMain
                        ))
                    account_save_text.setTextColor(
                        ContextCompat.getColor(this@AccountActivity,
                            R.color.colorMain
                        ))
                }

                else {
                    accountSaveButton.isClickable = false

                    accountSaveButton.backgroundTintList =
                        ContextCompat.getColorStateList(this@AccountActivity,
                            R.color.colorQuad
                        )
                    account_check_image.setColorFilter(
                        ContextCompat.getColor(this@AccountActivity,
                            R.color.colorTertiary
                        ))
                    account_save_text.setTextColor(
                        ContextCompat.getColor(this@AccountActivity,
                            R.color.colorTertiary
                        ))
                }
            }
        })

    }
}