package com.jerrwu.quintic.helpers

import android.content.Context
import com.jerrwu.quintic.R

object StringHelper {
    fun getString(id: Int, context: Context?): String {
        var string: String = ""
        if (context != null) {
            string = context.resources.getString(id)
        }
        return string
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

    fun getGreeting(time: String, context: Context?): String {
        return when (time.toInt()) {
            in 2 downTo 0 -> getString(R.string.greeting_late_night, context)
            in 5 downTo 3 -> getString(R.string.greeting_early_morning, context)
            in 10 downTo 6 -> getString(R.string.greeting_morning, context)
            in 13 downTo 11 -> getString(R.string.greeting_lunch, context)
            in 17 downTo 14 -> getString(R.string.greeting_afternoon, context)
            in 20 downTo 18 -> getString(R.string.greeting_evening, context)
            in 23 downTo 21 -> getString(R.string.greeting_night, context)
            24 -> getString(R.string.greeting_late_night, context)
            else -> "hie..."
        }
    }
}