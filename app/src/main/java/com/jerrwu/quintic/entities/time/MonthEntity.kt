package com.jerrwu.quintic.entities.time

import com.google.gson.annotations.SerializedName

data class MonthEntity(
    @SerializedName("month")
    val number: Int,

    @SerializedName("days")
    val days: List<DayEntity>?
) {
    companion object {
        val JAN = MonthEntity(1, null)
        val FEB = MonthEntity(2, null)
        val MAR = MonthEntity(3, null)
        val APR = MonthEntity(4, null)
        val MAY = MonthEntity(5, null)
        val JUN = MonthEntity(6, null)
        val JUL = MonthEntity(7, null)
        val AUG = MonthEntity(8, null)
        val SEP = MonthEntity(9, null)
        val OCT = MonthEntity(10, null)
        val NOV = MonthEntity(11, null)
        val DEC = MonthEntity(12, null)
    }

    constructor(number: Int):
            this(if (number % 12 == 0) 12 else number % 12, null)

    fun stringValue(): String {
        return when (number) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> "Unknown"
        }
    }
}