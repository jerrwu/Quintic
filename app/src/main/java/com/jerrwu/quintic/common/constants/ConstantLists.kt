package com.jerrwu.quintic.common.constants

import com.jerrwu.quintic.entities.mood.MoodEntity
import com.jerrwu.quintic.entities.time.Month

object ConstantLists {
    val moodSelectorOptions = listOf(
        MoodEntity.VERY_BAD, MoodEntity.ANGRY, MoodEntity.BAD, MoodEntity.NEUTRAL, MoodEntity.GOOD, MoodEntity.VERY_GOOD)
    val searchSpinnerOptionsDebug = listOf(
        "Title", "Content", "Mood", "Time", "Hours")
    val searchSpinnerOptions = listOf(
        "Title", "Content", "Mood", "Hours")
    val searchMoodOptions = listOf(
        MoodEntity.GOOD, MoodEntity.BAD, MoodEntity.NEUTRAL, MoodEntity.ANGRY, MoodEntity.VERY_GOOD, MoodEntity.VERY_BAD)
    val searchMonthOptions = listOf(
        Month.JAN, Month.FEB, Month.MAR, Month.APR, Month.MAY, Month.JUN,
        Month.JUL, Month.AUG, Month.SEP, Month.OCT, Month.NOV, Month.DEC)
}