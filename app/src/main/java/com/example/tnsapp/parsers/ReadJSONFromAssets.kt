package com.example.tnsapp.parsers

import android.content.Context
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

fun readJsonFromAssets(context: Context, fileName: String): String? {
    var json: String? = null
    try {
        val inputStream: InputStream = context.assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        json = String(buffer, Charset.defaultCharset())
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return json
}