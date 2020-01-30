package com.jerrwu.quintic.entities.mood

import com.jerrwu.quintic.R

class MoodEntity(var id: Int, var icOutline: Int, var icFilled: Int, var color: Int, var name: String) {
    companion object {
        val NONE = MoodEntity(0, 0, 0, 0, "")
        val VERY_BAD = MoodEntity(1, R.drawable.ic_cry_outline, R.drawable.ic_cry, R.color.purple, "Very Sad")
        val BAD = MoodEntity(2, R.drawable.ic_sad_outline, R.drawable.ic_sad, R.color.orange, "Sad")
        val NEUTRAL = MoodEntity(3, R.drawable.ic_neutral_outline, R.drawable.ic_neutral, R.color.yellow, "Neutral")
        val GOOD = MoodEntity(4, R.drawable.ic_happy_outline, R.drawable.ic_happy, R.color.blue, "Happy")
        val VERY_GOOD = MoodEntity(5, R.drawable.ic_excited_outline, R.drawable.ic_excited, R.color.green, "Very Happy")
        val ANGRY = MoodEntity(6, R.drawable.ic_angry_outline, R.drawable.ic_angry, R.color.red, "Angry")

        fun parse(mood: Int?): MoodEntity {
            return when (mood) {
                VERY_BAD.id -> VERY_BAD
                BAD.id -> BAD
                NEUTRAL.id -> NEUTRAL
                GOOD.id -> GOOD
                VERY_GOOD.id -> VERY_GOOD
                ANGRY.id -> ANGRY
                else -> NONE
            }
        }
    }
}