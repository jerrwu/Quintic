package com.jerrwu.quintic.entities.time


data class WeekdayEntity(
    val number: Int
) {
    companion object {
        val MONDAY = WeekdayEntity(1)
        val TUESDAY = WeekdayEntity(2)
        val WEDNESDAY = WeekdayEntity(3)
        val THURSDAY = WeekdayEntity(4)
        val FRIDAY = WeekdayEntity(5)
        val SATURDAY = WeekdayEntity(6)
        val SUNDAY = WeekdayEntity(7)
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