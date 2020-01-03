package com.jerrwu.quintic

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_account.*
import android.text.Editable
import android.text.TextWatcher
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat


class AccountActivity : AppCompatActivity() {
    private val buttonDisabled = ColorStateList.valueOf(Color.parseColor("#363636"))
    private val buttonEnabled = ColorStateList.valueOf(Color.parseColor("#A98274"))
    private val textDisabled = Color.parseColor("#747474")
    private val textEnabled = Color.parseColor("#242424")

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

                    if (isUsingNightModeResources()) {
                        accountSaveButton.backgroundTintList = buttonEnabled
                        account_check_image.setColorFilter(textEnabled)
                        account_save_text.setTextColor(textEnabled)
                    }

                    else {
                        accountSaveButton.backgroundTintList =
                            ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
                        account_check_image.setColorFilter(
                            ContextCompat.getColor(applicationContext, R.color.colorMain))
                        account_save_text.setTextColor(
                            ContextCompat.getColor(applicationContext, R.color.colorMain))
                    }
                }

                else {
                    accountSaveButton.isClickable = false

                    if (isUsingNightModeResources()) {
                        accountSaveButton.backgroundTintList = buttonDisabled
                        account_check_image.setColorFilter(textDisabled)
                        account_save_text.setTextColor(textDisabled)
                    }

                    else {
                        accountSaveButton.backgroundTintList =
                            ContextCompat.getColorStateList(applicationContext, R.color.colorQuad)
                        account_check_image.setColorFilter(
                            ContextCompat.getColor(applicationContext, R.color.colorTertiary))
                        account_save_text.setTextColor(
                            ContextCompat.getColor(applicationContext, R.color.colorTertiary))
                    }
                }
            }
        })

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