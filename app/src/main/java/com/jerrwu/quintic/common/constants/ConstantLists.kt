package com.jerrwu.quintic.common.constants

import com.jerrwu.quintic.entities.mood.MoodEntity

object ConstantLists {
    val moodSelectorOptions = listOf(
        MoodEntity.VERY_BAD, MoodEntity.ANGRY, MoodEntity.BAD, MoodEntity.NEUTRAL, MoodEntity.GOOD, MoodEntity.VERY_GOOD)
    val searchSpinnerOptionsDebug = listOf(
        "Title", "Content", "Mood", "Time")
    val searchSpinnerOptions = listOf(
        "Title", "Content", "Mood")
    val searchMoodOptions = listOf(
        MoodEntity.GOOD, MoodEntity.BAD, MoodEntity.NEUTRAL, MoodEntity.ANGRY, MoodEntity.VERY_GOOD, MoodEntity.VERY_BAD)
}