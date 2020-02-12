package com.jerrwu.quintic.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import com.jerrwu.quintic.BuildConfig

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