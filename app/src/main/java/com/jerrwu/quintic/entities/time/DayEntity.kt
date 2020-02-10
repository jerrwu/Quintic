package com.jerrwu.quintic.entities.time

import com.google.gson.annotations.SerializedName
import com.jerrwu.quintic.entities.entry.EntryEntity

data class DayEntity(
    @SerializedName("monthday")
    val dayOfMonth: Int,

    @SerializedName("weekday")
    val dayOfWeek: Int,

    var entries: Int
) {
    constructor(
        dayOfMonth: Int,
        dayOfWeek: Int
    ): this(dayOfMonth, dayOfWeek, 0)

}