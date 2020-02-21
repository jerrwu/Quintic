package com.jerrwu.quintic.entities.entry

import com.jerrwu.quintic.common.constants.Constants
import com.jerrwu.quintic.entities.mood.MoodEntity
import java.time.LocalDateTime

class EntryEntity(
    var id: Int,
    var ic: Int,
    var title: String,
    var content: String,
    var time: LocalDateTime,
    var mood: MoodEntity?,
    var tags: String?) {
    var isSelected: Boolean = false

    fun splitTags(): List<String>? {
        return tags?.split(Constants.TAG_DELIMITER)
    }
}