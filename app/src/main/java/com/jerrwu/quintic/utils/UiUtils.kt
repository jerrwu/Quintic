package com.jerrwu.quintic.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.EditTextFlow
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.main.MainActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable


object UiUtils {
    fun isUsingNightMode(configuration: Configuration): Boolean {
        return when (configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    fun hasNavBar(activity: Activity?): Boolean {
        if (activity != null) {
            val temporaryHidden = activity.window.decorView.visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION != 0
            if (temporaryHidden) return false
            val decorView = activity.window.decorView
            decorView.rootWindowInsets?.let{
                return it.stableInsetBottom != 0
            }
        }
        return true
    }

    fun showDialog(
        titleString: String, bodyString: String, textYes: String, textNo: String, activity: Context,
        func: ((List<EntryEntity>) -> Unit?), list: List<EntryEntity>
    ): Dialog {
        val dialog = Dialog(activity, R.style.DialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialogue)
        val title = dialog.findViewById(R.id.dialog_title) as TextView
        title.text = titleString
        val body = dialog.findViewById(R.id.dialog_body) as TextView
        body.text = bodyString
        val yesBtn = dialog.findViewById(R.id.yes_button) as Button
        yesBtn.text = textYes
        val noBtn = dialog.findViewById(R.id.no_button) as Button
        noBtn.text = textNo
        if (textYes == "") { yesBtn.visibility = View.GONE }
        yesBtn.setOnClickListener {
            func(list)
            dismissDialog(dialog) }
        noBtn.setOnClickListener { dismissDialog(dialog) }
        dialog.show()
        return dialog
    }

    fun showDialog(
        titleString: String, bodyString: String, textYes: String, textNo: String,
        activity: Context, funYes: ((funContext: Context) -> Unit)?, funNo: (funDialog: Dialog) -> Unit) {
        val dialog = Dialog(activity, R.style.DialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialogue)
        val title = dialog.findViewById(R.id.dialog_title) as TextView
        title.text = titleString
        val body = dialog.findViewById(R.id.dialog_body) as TextView
        body.text = bodyString
        val yesBtn = dialog.findViewById(R.id.yes_button) as Button
        yesBtn.text = textYes
        val noBtn = dialog.findViewById(R.id.no_button) as Button
        noBtn.text = textNo
        if (textYes == "") { yesBtn.visibility = View.GONE }
        yesBtn.setOnClickListener { if (funYes != null) funYes(activity) }
        noBtn.setOnClickListener { funNo(dialog) }
        dialog.show()
    }

    fun restartApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }

        Runtime.getRuntime().exit(0)
    }

    fun dismissDialog(dialog: Dialog) {
        dialog.dismiss()
    }
}