package com.jerrwu.quintic.utils;

import androidx.annotation.NonNull;

import com.jerrwu.quintic.entities.time.MonthEntity;

import java.util.ArrayList;
import java.util.List;

public class SafetyUtils {
    @NonNull
    public static <T> List<T> orEmpty(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    @NonNull
    public static MonthEntity nonNullMonth(MonthEntity month) {
        if (month == null) {
            return new MonthEntity(0, new ArrayList<>());
        }
        return month;
    }
}
