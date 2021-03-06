package com.jerrwu.quintic.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import com.jerrwu.quintic.BuildConfig

class MainDbHelper(context: Context) : BaseDbHelper(context) {
    companion object {
        const val DB_NAME = "MCards"
        const val DB_TABLE = "Cards"
        const val DB_COL_ID = "ID"
        const val DB_COL_ICON = "Image"
        const val DB_COL_TITLE = "Title"
        const val DB_COL_CONTENT = "Content"
        const val DB_COL_TIME = "DateTime"
        const val DB_COL_MOOD = "Mood"
        const val DB_COL_DATE_EXTERNAL = "Time"
        const val DB_COL_HOURS = "Hours"
        const val DB_COL_TAGS = "Tags"
    }

    val dbVersion: Int = BuildConfig.MAIN_DATABASE_VERSION

    val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $DB_TABLE (" +
                "$DB_COL_ID INTEGER PRIMARY KEY, $DB_COL_ICON INTEGER, $DB_COL_TITLE TEXT," +
                " $DB_COL_CONTENT TEXT, $DB_COL_TIME TEXT, $DB_COL_MOOD TEXT," +
                " $DB_COL_DATE_EXTERNAL TEXT, $DB_COL_HOURS TEXT, $DB_COL_TAGS TEXT);"

    override var sqlDb: SQLiteDatabase? = null

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
                Toast.makeText(this.context, "Main database created!", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) {
                db?.execSQL("ALTER TABLE $DB_TABLE ADD $DB_COL_MOOD INTEGER")
            }
            if (oldVersion < 3) {
                db?.execSQL("ALTER TABLE $DB_TABLE ADD $DB_COL_DATE_EXTERNAL TEXT")
            }
            if (oldVersion < 4)  {
                db?.execSQL("ALTER TABLE $DB_TABLE ADD $DB_COL_HOURS TEXT")
            }
            if (oldVersion < 5) {
                db?.execSQL("ALTER TABLE $DB_TABLE ADD $DB_COL_TAGS TEXT")
            }
            if (BuildConfig.DEBUG)
            (context as Activity).runOnUiThread {
                Toast.makeText(this.context, "Main database upgraded!", Toast.LENGTH_SHORT).show()
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
            sqlDb, projection, selection, selectionArgs, null, null, sorOrder)
    }

    override fun delete(selection: String, selectionArgs: Array<String>): Int? {
        return sqlDb?.delete(DB_TABLE, selection, selectionArgs)
    }

    override fun update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int? {
        return sqlDb?.update(DB_TABLE, values, selection, selectionArgs)
    }
}