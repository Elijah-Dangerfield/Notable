package com.dangerfield.core.notes

import com.dangerfield.core.notes.local.NotesDao
import com.dangerfield.core.notesapi.Note
import com.dangerfield.core.notes.local.NoteOperationStatus
import com.dangerfield.core.notes.remote.NotesRemoteDataSource
import com.dangerfield.core.notesapi.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstNoteRepository @Inject constructor(
private val dao: NotesDao,
private val remoteDataSource: NotesRemoteDataSource,
private val notesConflictResolver: NotesConflictResolver
): NoteRepository {

    override fun getNotesStream(): Flow<List<Note>> {
        return dao.getNotes()
            .map { localNotes ->
            val displayNotes = localNotes.filter { note ->
                NoteOperationStatus.valueOf(note.operationStatus) == NoteOperationStatus.Idle
            }
            return@map displayNotes.map { it.toDomainNote() }
        }
    }

    override suspend fun getNote(id: String): Note? = dao.getNoteById(id)?.toDomainNote()

    override suspend fun updateNote(note: Note): Boolean {
        dao.updateNote(note = note.toLocalEntity(NoteOperationStatus.PendingUpdate))

        remoteDataSource.updateNote(note.toRemoteEntity())

        dao.setNoteOperationStatus(note.id, NoteOperationStatus.Idle.name)

        return true
    }

    override suspend fun deleteNote(note: Note): Boolean {
        dao.updateNote(note = note.toLocalEntity(NoteOperationStatus.PendingDelete))

        remoteDataSource.deleteNote(note.id)

        dao.deleteNoteById(note.id)

        return true
    }

    override suspend fun createNote(note: Note): Boolean {
        dao.updateNote(note = note.toLocalEntity(NoteOperationStatus.PendingCreate))

        remoteDataSource.createNote(note.toRemoteEntity())

        dao.setNoteOperationStatus(note.id, NoteOperationStatus.Idle.name)

        return true
    }

    override suspend fun syncNotes() {
        val localNotes = dao.getNotes().first()
        val pendingDeletes = dao.getNotesWithOperationStatus(NoteOperationStatus.PendingDelete.name)
        val pendingCreates = dao.getNotesWithOperationStatus(NoteOperationStatus.PendingCreate.name)
        val pendingUpdates = dao.getNotesWithOperationStatus(NoteOperationStatus.PendingUpdate.name)
        val remoteNotes = remoteDataSource.getNotes()

        val resolvedNotes = notesConflictResolver.resolve(
            localNotes,
            pendingDeletes,
            pendingCreates,
            pendingUpdates,
            remoteNotes
        )

        dao.replaceNotes(resolvedNotes.map { it.toLocalEntity(NoteOperationStatus.Idle) })
    }
}

