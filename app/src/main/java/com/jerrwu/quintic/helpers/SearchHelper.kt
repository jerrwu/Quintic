package com.jerrwu.quintic.helpers

import com.jerrwu.quintic.entities.card.CardEntity
import com.jerrwu.quintic.entities.mood.MoodEntity
import java.time.LocalDateTime

object SearchHelper {
    fun performSearch(text: String, dbHelper: DbHelper): List<CardEntity> {
        val results = ArrayList<CardEntity>()

        val projections = arrayOf("ID", "Image", "Title", "Content", "DateTime", "Mood")
        val selectionArgs = arrayOf(text)
        val cursor = dbHelper.query(
            projections, "Title like?", selectionArgs, "ID"+" DESC")
        if (cursor.moveToFirst()) {

            do {
                val cdId = cursor.getInt(cursor.getColumnIndex("ID"))
                val cdIc = cursor.getInt(cursor.getColumnIndex("Image"))
                val cdTitle = cursor.getString(cursor.getColumnIndex("Title"))
                val cdCont = cursor.getString(cursor.getColumnIndex("Content"))
                val cdTime = cursor.getString(cursor.getColumnIndex("DateTime"))
                val cdMood = cursor.getInt(cursor.getColumnIndex("Mood"))

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
}