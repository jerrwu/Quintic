package com.jerrwu.quintic.utils

import com.google.gson.reflect.TypeToken
import com.jerrwu.quintic.entities.time.YearEntity
import java.lang.reflect.Type

object GsonUtils {
    val YearListType: Type = object : TypeToken<List<YearEntity>>() { }.type
}