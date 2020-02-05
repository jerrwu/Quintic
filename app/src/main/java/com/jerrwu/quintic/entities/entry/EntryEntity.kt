package com.jerrwu.quintic.entities.entry

import com.jerrwu.quintic.entities.mood.MoodEntity
import java.time.LocalDateTime

class EntryEntity(id: Int, ic: Int, title: String, content: String, time: LocalDateTime, mood: MoodEntity) {

    var isSelected: Boolean = false
    var title: String? = title
    var content: String? = content
    var ic: Int? = ic
    var id: Int? = id
    var time: LocalDateTime? = time
    var mood: MoodEntity? = mood
}