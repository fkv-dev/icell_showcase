package hu.fkv.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TimeManager {
    /**
     * A jelenlegi dátumot és időt adja ki formázva
     *
     * @return szövegesem a mostani idő. pl, 2025-01-01_11-11-11
     */
    fun getCurrentDateTimeAsString(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        return current.format(formatter)
    }
}
