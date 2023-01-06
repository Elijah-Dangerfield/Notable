package com.dangerfield.core.notesapi

data class Note(
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val color: Int? = null,
    val id: String
)
