package com.jerrwu.quintic.entities.time


data class Weekday(
    val number: Int
) {
    companion object {
        val MONDAY = Weekday(1)
        val TUESDAY = Weekday(2)
        val WEDNESDAY = Weekday(3)
        val THURSDAY = Weekday(4)
        val FRIDAY = Weekday(5)
        val SATURDAY = Weekday(6)
        val SUNDAY = Weekday(7)
    }

    override fun toString(): String {
        return when (number) {
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            7 -> "Sunday"
            else -> "Unknown"
        }
    }
}