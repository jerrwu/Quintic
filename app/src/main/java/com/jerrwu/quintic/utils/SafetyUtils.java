package com.jerrwu.quintic.utils;

import java.util.ArrayList;
import java.util.List;

public class SafetyUtils {
    public static <T> List<T> orEmpty(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }
}
