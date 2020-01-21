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

    fun getGreeting(time: String, context: Context?): String {
        return when (time.toInt()) {
            in 5 downTo 0 -> getString(R.string.greeting_early_morning_late_night, context)
            in 10 downTo 6 -> getString(R.string.greeting_morning, context)
            in 13 downTo 11 -> getString(R.string.greeting_lunch, context)
            in 17 downTo 14 -> getString(R.string.greeting_afternoon, context)
            in 21 downTo 18 -> getString(R.string.greeting_evening, context)
            in 24 downTo 22 -> getString(R.string.greeting_night, context)
            else -> getString(R.string.greeting_default, context)
        }
    }
}