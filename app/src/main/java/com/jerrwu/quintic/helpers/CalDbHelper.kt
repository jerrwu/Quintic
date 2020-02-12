package com.jerrwu.quintic.helpers

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import com.jerrwu.quintic.BuildConfig

class CalDbHelper(context: Context) : BaseDbHelper(context) {
    companion object {
        const val DB_NAME = "MCal"
        const val DB_TABLE = "Cal"
        const val DB_COL_ID = "ID"
        const val DB_COL_DATE = "Date"
        const val DB_COL_ENTRIES = "Entries"
    }

    val dbVersion: Int = BuildConfig.CAL_DATABASE_VERSION

    val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $DB_TABLE (" +
                "$DB_COL_ID INTEGER PRIMARY KEY, $DB_COL_DATE TEXT, $DB_COL_ENTRIES INTEGER);"

    private var sqlDb: SQLiteDatabase? = null

    init {
        val db = DatabaseHelperEntries(context)
        sqlDb = db.writableDatabase
    }

    inner class DatabaseHelperEntries : SQLiteOpenHelper {
        var context: Context? = null

        constructor(context: Context) : super(context, DB_NAME, null, dbVersion) {
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(sqlCreateTable)
            if (BuildConfig.DEBUG)
            (context as Activity).runOnUiThread {
                Toast.makeText(this.context, "Calendar database created!", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            if (BuildConfig.DEBUG)
            (context as Activity).runOnUiThread {
                Toast.makeText(this.context, "Calendar database upgraded!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun insert(values: ContentValues): Long? {
        return sqlDb?.insert(DB_TABLE, "", values)
    }

    override fun query(
        projection: Array<String>, selection: String,
        selectionArgs: Array<String>, sorOrder: String): Cursor {
        val qb = SQLiteQueryBuilder()
        qb.tables = DB_TABLE
        return qb.query(
            sqlDb, projection, selection, selectionArgs, null, null, null)
    }

    override fun delete(selection: String, selectionArgs: Array<String>): Int? {
        return sqlDb?.delete(DB_TABLE, selection, selectionArgs)
    }

    override fun update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int? {
        return sqlDb?.update(DB_TABLE, values, selection, selectionArgs)
    }
}