package com.dangerfield.core.notesapi

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Note(
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val color: Int,
    val id: String
)

fun Note.getReadableUpdatedAtDate(): String? {
    val formatter = SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm aaa", Locale.ENGLISH)
    return try {
        val date = Date(this.updatedAt)
        formatter.format(date).toString()
    } catch (e: ParseException) {
        Log.d("Notable", e.message)
        null
    }
}

fun Note.getReadableUpdatedAtDateShort(): String? {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
    return try {
        val date = Date(this.updatedAt)
        formatter.format(date).toString()
    } catch (e: ParseException) {
        Log.d("Notable", e.message)
        null
    }
}
