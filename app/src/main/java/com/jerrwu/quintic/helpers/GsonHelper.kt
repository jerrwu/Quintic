package com.jerrwu.quintic.helpers

import com.google.gson.reflect.TypeToken
import com.jerrwu.quintic.entities.time.YearEntity
import java.lang.reflect.Type

object GsonHelper {
    val YearListType: Type = object : TypeToken<List<YearEntity>>() { }.type
}