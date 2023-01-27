package com.dangerfield.core.notes.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NOTES")
data class LocalNoteEntity(
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val color: Int,
    val operationStatus: String,
    @PrimaryKey val id: String
)
