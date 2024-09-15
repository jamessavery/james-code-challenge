package com.example.james_code_challenge.domain.util

import com.example.james_code_challenge.domain.util.TimeDateUtils.Companion.TIMEDATE_STRING_CUTOFF
import com.example.james_code_challenge.domain.util.TimeDateUtils.Companion.OUTPUT_PATTERN
import com.example.james_code_challenge.domain.util.TimeDateUtils.Companion.PATTERN_STRING
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TimeDateUtils {
    companion object {
        const val PATTERN_STRING = "yyyy-MM-dd'T'HH:mm:ss"
        const val OUTPUT_PATTERN = "dd/MM/yyyy"
        const val TIMEDATE_STRING_CUTOFF = 19
    }
}

fun String.toLocalDate(): String {
    val pattern = DateTimeFormatter.ofPattern(PATTERN_STRING)
    val outputDatePattern = DateTimeFormatter.ofPattern(OUTPUT_PATTERN)

    return try {
        val localDate =
            // We are not concerned with milliseconds, cut that part off from String being passed into function
            LocalDateTime.parse(this.take(TIMEDATE_STRING_CUTOFF), pattern).toLocalDate()
        localDate.format(outputDatePattern)
    } catch (e: DateTimeParseException) {
        // Prevent crash, but ensure to report exception
        val localDate = LocalDateTime.now().toLocalDate().toString()
        return localDate.format(outputDatePattern)
    }
}
