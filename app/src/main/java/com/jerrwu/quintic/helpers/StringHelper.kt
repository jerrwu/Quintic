package com.jerrwu.quintic.helpers

import android.content.Context
import com.jerrwu.quintic.R
import com.jerrwu.quintic.common.constants.Constants
import kotlin.random.Random

object StringHelper {
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
        return if (Math.random() < 0.95) {
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
}