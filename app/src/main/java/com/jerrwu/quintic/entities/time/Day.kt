package com.jerrwu.quintic.entities.time

import com.jerrwu.quintic.entities.entry.EntryEntity
import java.time.DayOfWeek

data class Day(
    val dayOfMonth: Int,
    val dayOfWeek: Weekday,
    var entryList: List<EntryEntity>
)