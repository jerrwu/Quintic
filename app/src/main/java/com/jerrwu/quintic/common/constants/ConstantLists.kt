package com.jerrwu.quintic.common.constants

import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.entities.time.MonthEntity

object ConstantLists {
    val moodSelectorOptions = listOf(
        MoodEntity.VERY_BAD, MoodEntity.ANGRY, MoodEntity.BAD, MoodEntity.NEUTRAL, MoodEntity.GOOD, MoodEntity.VERY_GOOD)
    val searchSpinnerOptions = listOf(
        "Title", "Content", "Mood", "Time", "Hours")
    val searchMoodOptions = listOf(
        MoodEntity.GOOD, MoodEntity.BAD, MoodEntity.NEUTRAL, MoodEntity.ANGRY, MoodEntity.VERY_GOOD, MoodEntity.VERY_BAD)
    val searchMonthOptions = listOf(
        MonthEntity.JAN, MonthEntity.FEB, MonthEntity.MAR, MonthEntity.APR, MonthEntity.MAY, MonthEntity.JUN,
        MonthEntity.JUL, MonthEntity.AUG, MonthEntity.SEP, MonthEntity.OCT, MonthEntity.NOV, MonthEntity.DEC)
    val searchHoursOptions = listOf(
        Constants.MORNING, Constants.NOON, Constants.AFTERNOON, Constants.EVENING)
    val calHeaders = listOf(
        "M", "T", "W", "Th", "F", "Sa", "S")
}