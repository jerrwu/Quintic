package com.jerrwu.quintic.utils

import android.content.ContentValues
import android.content.Context

open class BaseDbHelper(context: Context) {

    open fun insert(values: ContentValues): Long? {
        return null
    }

    open fun query(
        projection: Array<String>, selection: String,
        selectionArgs: Array<String>, sorOrder: String): Any {
        return 0
    }

    open fun delete(selection: String, selectionArgs: Array<String>): Int? {
        return null
    }

    open fun update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int? {
        return null
    }
}