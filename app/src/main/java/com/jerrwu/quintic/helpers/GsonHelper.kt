package com.jerrwu.quintic.helpers

import com.google.gson.reflect.TypeToken
import com.jerrwu.quintic.entities.time.YearEntity

object GsonHelper {
    val YearListType = object : TypeToken<List<YearEntity>>() { }.type
}