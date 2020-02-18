package com.jerrwu.quintic.utils

import android.content.Context
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

object FileUtils {

    fun fromAssetsJson(context: Context, filename: String): String? {
        val json: String?
        json = try {
            val inputStream: InputStream = context.getAssets().open(filename)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val charset = Charset.forName("UTF-8")
            String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

}