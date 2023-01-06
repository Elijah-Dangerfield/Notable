package com.dangerfield.core.notes.remote

interface NotesRemoteDataSource {

    suspend fun deleteNote(id: String)

    suspend fun updateNote(note: RemoteNoteEntity)

    suspend fun createNote(note: RemoteNoteEntity)

    suspend fun getNotes(): List<RemoteNoteEntity>
}

