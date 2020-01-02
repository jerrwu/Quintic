package com.jerrwu.quintic

import java.time.LocalDateTime

class Card(id: Int, ic: Int, title: String, content: String, time: LocalDateTime) {
    var title: String? = title
    var content: String? = content
    var ic: Int? = ic
    var id: Int? = id
    var time: LocalDateTime? = time
}