package com.jerrwu.quintic.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.BaseActivity
import com.jerrwu.quintic.common.EditTextFlow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account.*


class AccountActivity : BaseActivity() {
    companion object {
        val TAG = AccountActivity::class.java.simpleName
    }

    var mSetName: String? = ""

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        mSetName = prefs.getString("name", "")
        account_edit_text.setText(mSetName)

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

        account_edit_text.addTextWatcher()
            .filter { it.type == EditTextFlow.Type.AFTER }
            .map { it.query }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                toggleSaveButton(it)
            }
    }

    @UiThread
    private fun toggleSaveButton(text: String) {
        if (text.isNotEmpty() && text != mSetName) {
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
}