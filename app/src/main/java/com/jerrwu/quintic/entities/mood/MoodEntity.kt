package com.jerrwu.quintic.entities.mood

import com.jerrwu.quintic.R

class MoodEntity(var mood: Int, var icOutline: Int, var icFilled: Int, var color: Int) {
    companion object {
        val NONE = MoodEntity(-500, 0, 0, 0)
        val VERY_BAD = MoodEntity(-2, R.drawable.ic_cry_outline, R.drawable.ic_cry, R.color.red)
        val BAD = MoodEntity(-1, R.drawable.ic_sad_outline, R.drawable.ic_sad, R.color.orange)
        val NEUTRAL = MoodEntity(0, R.drawable.ic_neutral_outline, R.drawable.ic_neutral, R.color.yellow)
        val GOOD = MoodEntity(1, R.drawable.ic_happy_outline, R.drawable.ic_happy, R.color.green)
        val VERY_GOOD = MoodEntity(2, R.drawable.ic_excited_outline, R.drawable.ic_excited, R.color.blue)

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
}