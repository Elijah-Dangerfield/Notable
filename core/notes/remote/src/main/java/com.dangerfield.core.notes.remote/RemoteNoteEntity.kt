package com.dangerfield.core.notes.remote

data class RemoteNoteEntity(
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val color: Int? = null,
    val id: String
)
