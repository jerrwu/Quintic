package com.jerrwu.quintic.helpers

import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.mood.MoodEntity
import java.time.LocalDateTime


object SearchHelper {
    const val ORDER_ASCENDING = "ASC"
    const val ORDER_DESCENDING = "DESC"

    fun performExactSearch(text: String, dbHelper: DbHelper, order: String, column: String?): List<EntryEntity> {
        val results = ArrayList<EntryEntity>()

        if (text.isBlank()) return results

        val projections = arrayOf(
            DbHelper.DB_COL_ID,
            DbHelper.DB_COL_ICON,
            DbHelper.DB_COL_TITLE,
            DbHelper.DB_COL_CONTENT,
            DbHelper.DB_COL_TIME,
            DbHelper.DB_COL_MOOD)
        val selectionArgs = arrayOf(text)
        val cursor = dbHelper.query(
            projections, "$column LIKE ?", selectionArgs, "ID $order"
        )
        if (cursor.moveToFirst()) {

            do {
                val cdId = cursor.getInt(cursor.getColumnIndex(DbHelper.DB_COL_ID))
                val cdIc = cursor.getInt(cursor.getColumnIndex(DbHelper.DB_COL_ICON))
                val cdTitle = cursor.getString(cursor.getColumnIndex(DbHelper.DB_COL_TITLE))
                val cdCont = cursor.getString(cursor.getColumnIndex(DbHelper.DB_COL_CONTENT))
                val cdTime = cursor.getString(cursor.getColumnIndex(DbHelper.DB_COL_TIME))
                val cdMood = cursor.getString(cursor.getColumnIndex(DbHelper.DB_COL_MOOD))

                results.add(
                    EntryEntity(
                        cdId,
                        cdIc,
                        cdTitle,
                        cdCont,
                        LocalDateTime.parse(cdTime),
                        MoodEntity.parse(cdMood)
                    )
                )

            } while (cursor.moveToNext())
        }
        cursor.close()

        return results
    }

    fun performSearch(text: String, dbHelper: DbHelper, column: String?): List<EntryEntity> {
        return performSearch(text, dbHelper, ORDER_DESCENDING, column)
    }

    fun performSearch(text: String, dbHelper: DbHelper, order: String, column: String?): List<EntryEntity> {
        val results = ArrayList<EntryEntity>()

        if (text.isBlank()) return results

        return performExactSearch("%$text%", dbHelper, order, column)
    }

}