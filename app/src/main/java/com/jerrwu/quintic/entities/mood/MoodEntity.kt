package com.jerrwu.quintic.entities.mood

import com.jerrwu.quintic.R

class MoodEntity(mood: Int?, icOutline: Int?, icFilled: Int?) {
    companion object {
        val VERY_BAD = MoodEntity(-2, R.drawable.ic_cry_outline, R.drawable.ic_cry)
        val BAD = MoodEntity(-1, R.drawable.ic_sad_outline, R.drawable.ic_sad)
        val NEUTRAL = MoodEntity(0, R.drawable.ic_neutral_outline, R.drawable.ic_neutral)
        val GOOD = MoodEntity(1, R.drawable.ic_happy_outline, R.drawable.ic_happy)
        val VERY_GOOD = MoodEntity(2, R.drawable.ic_excited_outline, R.drawable.ic_excited)

        fun parse(mood: Int?): MoodEntity {
            return when (mood) {
                -2 -> VERY_BAD
                -1 -> BAD
                0 -> NEUTRAL
                1 -> GOOD
                2 -> VERY_GOOD
                else -> NEUTRAL
            }
        }
    }

    var mood: Int? = mood
    var icOutline: Int? = icOutline
    var icFilled: Int? = icFilled


}