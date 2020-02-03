package com.jerrwu.quintic.helpers

import com.jerrwu.quintic.entities.card.CardEntity
import com.jerrwu.quintic.entities.mood.MoodEntity
import java.time.LocalDateTime


object SearchHelper {
    const val ORDER_ASCENDING = "ASC"
    const val ORDER_DESCENDING = "DESC"

    // TODO: proper date and mood search

    fun performSearch(text: String, dbHelper: DbHelper, column: String?): List<CardEntity> {
        return performSearch(text, dbHelper, ORDER_DESCENDING, column)
    }

    fun performSearch(text: String, dbHelper: DbHelper, order: String, column: String?): List<CardEntity> {
        val results = ArrayList<CardEntity>()

        if (text == "") return results

        val projections = arrayOf(
            DbHelper.DB_COL_ID,
            DbHelper.DB_COL_ICON,
            DbHelper.DB_COL_TITLE,
            DbHelper.DB_COL_CONTENT,
            DbHelper.DB_COL_TIME,
            DbHelper.DB_COL_MOOD)
        val selectionArgs = arrayOf("%$text%")
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
                        CardEntity(
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

    private fun <T> Array<T>.mapInPlace(transform: (T) -> T) {
        for (i in this.indices) {
            this[i] = transform(this[i])
        }
    }
}