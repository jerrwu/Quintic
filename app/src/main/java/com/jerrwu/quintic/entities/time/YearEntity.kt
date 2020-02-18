package com.jerrwu.quintic.entities.time

import com.google.gson.annotations.SerializedName

data class YearEntity(
    @SerializedName("year")
    val number: Int,

    @SerializedName("months")
    val months: List<MonthEntity>?
)