package com.jerrwu.quintic.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbHelper(context: Context) {

    var dbName = "MCards"
    var dbTable = "Cards"
    private var colID = "ID"
    private var colIc = "Image"
    private var colTitle = "Title"
    private var colCont = "Content"
    private var colTime = "DateTime"
    private var colMood = "Mood"
    var dbVersion = 2

    val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $dbTable (" +
                "$colID INTEGER PRIMARY KEY, $colIc INTEGER, $colTitle TEXT," +
                " $colCont TEXT, $colTime TEXT, $colMood INTEGER );"

    private var sqlDB: SQLiteDatabase? = null

    init {
        val db = DatabaseHelperEntries(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelperEntries : SQLiteOpenHelper {
        var context: Context? = null

        constructor(context: Context) : super(context, dbName, null, dbVersion) {
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context, "Database created!", Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2)
                db!!.execSQL("ALTER TABLE $dbTable ADD $colMood INTEGER")
            Toast.makeText(this.context, "Database upgraded!", Toast.LENGTH_SHORT).show()
        }


    }

    fun insert(values: ContentValues): Long {
        return sqlDB!!.insert(dbTable, "", values)
    }

    fun query(
        projection: Array<String>, selection: String,
        selectionArgs: Array<String>, sorOrder: String): Cursor {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbTable
        return qb.query(
            sqlDB, projection, selection, selectionArgs, null, null, sorOrder)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {
        return sqlDB!!.delete(dbTable, selection, selectionArgs)
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        return sqlDB!!.update(dbTable, values, selection, selectionArgs)
    }
}