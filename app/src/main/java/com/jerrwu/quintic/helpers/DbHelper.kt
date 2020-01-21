package com.jerrwu.quintic.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbHelper {

    var dbName = "MCards"
    var dbTable = "Cards"
    var colID = "ID"
    var colIc = "Image"
    var colTitle = "Title"
    var colCont = "Content"
    var colTime = "DateTime"
    var dbVersion = 1

    val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $dbTable (" +
                "$colID INTEGER PRIMARY KEY, $colIc INTEGER, $colTitle TEXT," +
                " $colCont TEXT, $colTime TEXT);"

    private var sqlDB: SQLiteDatabase? = null

    constructor(context: Context) {
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
            Toast.makeText(this.context, "database created...", Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table if Exists $dbTable")
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