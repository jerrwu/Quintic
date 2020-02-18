package com.jerrwu.quintic.utils

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.Constants
import com.jerrwu.quintic.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*


object StringUtils {
    fun getString(id: Int, context: Context?): String {
        var string = ""
        if (context != null) {
            string = context.resources.getString(id)
        }
        return string
    }

    fun getHours(hour: Int?): String {
        return when (hour) {
            in 10 downTo 5 -> Constants.MORNING
            in 13 downTo 11 -> Constants.NOON
            in 17 downTo 14 -> Constants.AFTERNOON
            in 24 downTo 18 -> Constants.EVENING
            in 4 downTo 0 -> Constants.EVENING
            else -> "Unknown"
        }
    }

    fun getDaySection(time: String, context: Context?): String {
        return when (time.toInt()) {
            in 2 downTo 0 -> getString(R.string.late_night, context)
            in 5 downTo 3 -> getString(R.string.early_morning, context)
            in 10 downTo 6 -> getString(R.string.morning, context)
            in 13 downTo 11 -> getString(R.string.midday, context)
            in 17 downTo 14 -> getString(R.string.afternoon, context)
            in 20 downTo 18 -> getString(R.string.evening, context)
            in 23 downTo 21 -> getString(R.string.night, context)
            24 -> getString(R.string.late_night, context)
            else -> "hie..."
        }
    }

    fun intOfMonth(month: String): Int {
        return when {
            "january".contains(month, ignoreCase = true) -> {
                1
            }
            "february".contains(month, ignoreCase = true) -> {
                2
            }
            "march".contains(month, ignoreCase = true) -> {
                3
            }
            "april".contains(month, ignoreCase = true) -> {
                4
            }
            "may".contains(month, ignoreCase = true) -> {
                5
            }
            "june".contains(month, ignoreCase = true) -> {
                6
            }
            "july".contains(month, ignoreCase = true) -> {
                7
            }
            "august".contains(month, ignoreCase = true) -> {
                8
            }
            "september".contains(month, ignoreCase = true) -> {
                9
            }
            "october".contains(month, ignoreCase = true) -> {
                10
            }
            "november".contains(month, ignoreCase = true) -> {
                11
            }
            "december".contains(month, ignoreCase = true) -> {
                12
            }
            else -> {
                0
            }
        }
    }

    fun parseMonth(month: String): String {
        return when {
            "january".contains(month, ignoreCase = true) -> {
                "01"
            }
            "february".contains(month, ignoreCase = true) -> {
                "02"
            }
            "march".contains(month, ignoreCase = true) -> {
                "03"
            }
            "april".contains(month, ignoreCase = true) -> {
                "04"
            }
            "may".contains(month, ignoreCase = true) -> {
                "05"
            }
            "june".contains(month, ignoreCase = true) -> {
                "06"
            }
            "july".contains(month, ignoreCase = true) -> {
                "07"
            }
            "august".contains(month, ignoreCase = true) -> {
                "08"
            }
            "september".contains(month, ignoreCase = true) -> {
                "09"
            }
            "october".contains(month, ignoreCase = true) -> {
                "10"
            }
            "november".contains(month, ignoreCase = true) -> {
                "11"
            }
            "december".contains(month, ignoreCase = true) -> {
                "12"
            }
            else -> {
                "00"
            }
        }
    }

    fun getGreeting(time: String, context: Context?): String {
        return if (Math.random() < 0.99) {
            when (time.toInt()) {
                in 2 downTo 0 -> getString(R.string.greeting_late_night, context)
                in 5 downTo 3 -> getString(R.string.greeting_early_morning, context)
                in 10 downTo 6 -> getString(R.string.greeting_morning, context)
                in 12 downTo 11 -> getString(R.string.greeting_lunch, context)
                in 17 downTo 13 -> getString(R.string.greeting_afternoon, context)
                in 20 downTo 18 -> getString(R.string.greeting_evening, context)
                in 23 downTo 21 -> getString(R.string.greeting_night, context)
                24 -> getString(R.string.greeting_late_night, context)
                else -> "hie..."
            }
        } else {
            getString(R.string.jerry_epic, context)
        }
    }

    fun makeSnackbar(text: String, activity: Activity?) {
        if (activity != null) {

            val snackbar = Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT)
            snackbar.apply { view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams).apply {
                setMargins(
                    leftMargin, topMargin, rightMargin, (activity as MainActivity).bottomNavigation.height)} }
            snackbar.view.elevation = 0F

            val textView= snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.typeface = ResourcesCompat.getFont(activity, R.font.productsansregular)
            textView.textSize = 14F
            snackbar.show()
        }
    }

    fun isInteger(s: String): Boolean {
        return isInteger(s, 10)
    }

    fun isInteger(s: String, radix: Int): Boolean {
        if (s.isEmpty()) return false
        for (i in s.indices) {
            if (i == 0 && s[i] == '-') {
                return if (s.length == 1) false else continue
            }
            if (Character.digit(s[i], radix) < 0) return false
        }
        return true
    }
}