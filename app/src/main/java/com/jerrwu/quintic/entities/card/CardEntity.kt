package com.jerrwu.quintic.entities.card

import java.time.LocalDateTime

class CardEntity(id: Int, ic: Int, title: String, content: String, time: LocalDateTime, mood: Int) {
    companion object {
        const val MOOD_VERY_BAD = -2
        const val MOOD_BAD = -1
        const val MOOD_NEUTRAL = 0
        const val MOOD_GOOD = 1
        const val MOOD_VERY_GOOD = 2
    }

    var isSelected: Boolean = false
    var title: String? = title
    var content: String? = content
    var ic: Int? = ic
    var id: Int? = id
    var time: LocalDateTime? = time
    var mood: Int? = mood
}