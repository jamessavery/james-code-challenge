package com.example.james_code_challenge.domain.util

import android.content.Context
import java.io.IOException

object JsonUtils {

    fun getJsonFromAssets(context: Context, fileName: String): String? {
        val jsonString: String = try {
            val inputStream = context.assets.open(fileName)

            val size = inputStream.available() // Thread safe way to get size
            val buffer = ByteArray(size) // Creates a buffer of the appropriate size

            inputStream.read(buffer) // Prepares buffer to be passed into String below
            inputStream.close()

            String(buffer) // Add contents of buffer to form a String
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return jsonString
    }

}