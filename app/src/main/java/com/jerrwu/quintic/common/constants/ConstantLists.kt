package com.jerrwu.quintic.common.constants

import com.jerrwu.quintic.entities.mood.MoodEntity

object ConstantLists {
    val moodSelectorOptions = listOf(
        MoodEntity.VERY_BAD, MoodEntity.ANGRY, MoodEntity.BAD, MoodEntity.NEUTRAL, MoodEntity.GOOD, MoodEntity.VERY_GOOD)
    val searchSpinnerOptions = listOf(
        "Title", "Content", "Mood", "Time")
}