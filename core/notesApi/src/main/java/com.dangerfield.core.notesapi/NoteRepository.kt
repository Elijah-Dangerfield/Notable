package com.dangerfield.core.notesapi

import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotesStream(): Flow<List<Note>>

    suspend fun getNote(id: String): Note?

    suspend fun updateNote(note: Note): Boolean

    suspend fun deleteNote(note: Note): Boolean

    suspend fun createNote(note: Note): Boolean

    suspend fun syncNotes()
}
