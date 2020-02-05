package com.jerrwu.quintic.entities.time

data class Month(
    val number: Int,
    val days: List<Day>?
) {
    companion object {
        val JAN = Month(1, null)
        val FEB = Month(2, null)
        val MAR = Month(3, null)
        val APR = Month(4, null)
        val MAY = Month(5, null)
        val JUN = Month(6, null)
        val JUL = Month(7, null)
        val AUG = Month(8, null)
        val SEP = Month(9, null)
        val OCT = Month(10, null)
        val NOV = Month(11, null)
        val DEC = Month(12, null)
    }

    constructor(number: Int):
            this(if (number % 12 == 0) 12 else number % 12, null)

    override fun toString(): String {
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