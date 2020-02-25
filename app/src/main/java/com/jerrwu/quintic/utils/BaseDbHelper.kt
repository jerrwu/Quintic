package com.jerrwu.quintic.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

abstract class BaseDbHelper(context: Context) {

    protected abstract var sqlDb: SQLiteDatabase?

    open fun close() {
        sqlDb?.close()
    }

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