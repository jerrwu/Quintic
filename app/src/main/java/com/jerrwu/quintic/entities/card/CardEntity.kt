package com.jerrwu.quintic.entities.card

import java.time.LocalDateTime

class CardEntity(id: Int, ic: Int, title: String, content: String, time: LocalDateTime) {
    var title: String? = title
    var content: String? = content
    var ic: Int? = ic
    var id: Int? = id
    var time: LocalDateTime? = time
}