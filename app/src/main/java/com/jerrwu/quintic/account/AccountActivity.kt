package com.jerrwu.quintic.account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.jerrwu.quintic.R
import kotlinx.android.synthetic.main.activity_account.*


class AccountActivity : AppCompatActivity() {
    companion object {
        val TAG = AccountActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        account_back_button.setOnClickListener {
            finish()
        }

        account_save_button.setOnClickListener {
            val editor = prefs.edit()
            editor.putString("name", account_edit_text.text.toString())
            editor.putBoolean("setNameRem", false)
            editor.apply()
            finish()
        }
        account_save_button.isClickable = false

        account_edit_text.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length != 0) {
                    account_save_button.isClickable = true

                    account_save_button.backgroundTintList =
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
                    account_save_button.isClickable = false

                    account_save_button.backgroundTintList =
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