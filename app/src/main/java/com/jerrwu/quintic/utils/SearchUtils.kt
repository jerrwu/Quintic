package com.jerrwu.quintic.utils

import android.database.Cursor
import com.jerrwu.quintic.entities.entry.EntryEntity
import com.jerrwu.quintic.entities.mood.MoodEntity
import java.time.LocalDateTime


object SearchUtils {
    const val ORDER_ASCENDING = "ASC"
    const val ORDER_DESCENDING = "DESC"

    fun performCalEntryCountSearch(dateString: String, baseDbHelper: BaseDbHelper): List<Int> {
        var result = listOf(0, 0)

        if (dateString.isBlank()) return result

        val projections = arrayOf(CalDbHelper.DB_COL_ID, CalDbHelper.DB_COL_ENTRIES)
        val selectionArgs = arrayOf(dateString)
        val cursor = baseDbHelper.query(
            projections, "${CalDbHelper.DB_COL_DATE} LIKE ?", selectionArgs, ""
        ) as Cursor
        if (cursor.moveToFirst()) {

            do {
                val calId = cursor.getInt(cursor.getColumnIndex(CalDbHelper.DB_COL_ID))
                val entryCount = cursor.getInt(cursor.getColumnIndex(CalDbHelper.DB_COL_ENTRIES))
                result = listOf(calId, entryCount)

            } while (cursor.moveToNext())
        }
        cursor.close()

        return result
    }

    fun performExactSearch(text: String, baseDbHelper: BaseDbHelper, order: String, column: String?): List<EntryEntity> {
        val results = ArrayList<EntryEntity>()

        if (text.isBlank()) return results

        val projections = arrayOf(
            MainDbHelper.DB_COL_ID,
            MainDbHelper.DB_COL_ICON,
            MainDbHelper.DB_COL_TITLE,
            MainDbHelper.DB_COL_CONTENT,
            MainDbHelper.DB_COL_TIME,
            MainDbHelper.DB_COL_MOOD,
            MainDbHelper.DB_COL_TAGS)
        val selectionArgs = arrayOf(text)
        val cursor = baseDbHelper.query(
            projections, "$column LIKE ?", selectionArgs, "ID $order"
        ) as Cursor
        if (cursor.moveToFirst()) {

            do {
                val entryId = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ID))
                val entryIc = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ICON))
                val entryTitle = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TITLE))
                val entryContent = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_CONTENT))
                val entryTime = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TIME))
                val entryMood = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_MOOD))
                val entryTags = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TAGS))

                results.add(
                    EntryEntity(
                        entryId,
                        entryIc,
                        entryTitle,
                        entryContent,
                        LocalDateTime.parse(entryTime),
                        MoodEntity.parse(entryMood),
                        entryTags
                    )
                )

            } while (cursor.moveToNext())
        }
        cursor.close()

        return results
    }

    fun performSearch(text: String, baseDbHelper: BaseDbHelper, column: String?): List<EntryEntity> {
        return performSearch(text, baseDbHelper, ORDER_DESCENDING, column)
    }

    fun performSearch(text: String, baseDbHelper: BaseDbHelper, order: String, column: String?): List<EntryEntity> {
        val results = ArrayList<EntryEntity>()

        if (text.isBlank()) return results

        return performExactSearch("%$text%", baseDbHelper, order, column)
    }

}