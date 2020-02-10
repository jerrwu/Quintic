package com.jerrwu.quintic.helpers

import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.mood.MoodEntity
import java.time.LocalDateTime


object SearchHelper {
    const val ORDER_ASCENDING = "ASC"
    const val ORDER_DESCENDING = "DESC"

    fun performExactSearch(text: String, mainDbHelper: MainDbHelper, order: String, column: String?): List<EntryEntity> {
        val results = ArrayList<EntryEntity>()

        if (text.isBlank()) return results

        val projections = arrayOf(
            MainDbHelper.DB_COL_ID,
            MainDbHelper.DB_COL_ICON,
            MainDbHelper.DB_COL_TITLE,
            MainDbHelper.DB_COL_CONTENT,
            MainDbHelper.DB_COL_TIME,
            MainDbHelper.DB_COL_MOOD)
        val selectionArgs = arrayOf(text)
        val cursor = mainDbHelper.query(
            projections, "$column LIKE ?", selectionArgs, "ID $order"
        )
        if (cursor.moveToFirst()) {

            do {
                val cdId = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ID))
                val cdIc = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ICON))
                val cdTitle = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TITLE))
                val cdCont = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_CONTENT))
                val cdTime = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TIME))
                val cdMood = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_MOOD))

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

    fun performSearch(text: String, mainDbHelper: MainDbHelper, column: String?): List<EntryEntity> {
        return performSearch(text, mainDbHelper, ORDER_DESCENDING, column)
    }

    fun performSearch(text: String, mainDbHelper: MainDbHelper, order: String, column: String?): List<EntryEntity> {
        val results = ArrayList<EntryEntity>()

        if (text.isBlank()) return results

        return performExactSearch("%$text%", mainDbHelper, order, column)
    }

}