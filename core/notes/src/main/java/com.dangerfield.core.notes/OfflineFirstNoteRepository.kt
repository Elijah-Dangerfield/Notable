package com.dangerfield.core.notes

import android.util.Log
import com.dangerfield.core.notes.local.NoteOperationStatus
import com.dangerfield.core.notes.local.NotesDao
import com.dangerfield.core.notes.remote.NotesRemoteDataSource
import com.dangerfield.core.notesapi.Note
import com.dangerfield.core.notesapi.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstNoteRepository @Inject constructor(
    private val dao: NotesDao,
    private val remoteDataSource: NotesRemoteDataSource,
    private val notesConflictResolver: NotesConflictResolver
) : NoteRepository {

    override fun getNotesStream(): Flow<List<Note>> {
        Log.d("Elijah", "Getting the notes from the local DB")
        return dao.getNotes()
            .map { localNotes ->
                Log.d("Elijah", "Notes from the DB are: ${localNotes.size}")

                val displayNotes = localNotes.filter { note ->
                    NoteOperationStatus.valueOf(note.operationStatus) == NoteOperationStatus.Idle
                }
                return@map displayNotes.map { it.toDomainNote() }
            }
    }

    override suspend fun getNote(id: String): Note? = dao.getNoteById(id)?.toDomainNote()

    override suspend fun updateNote(note: Note): Boolean {
        Log.d("Elijah", "setting note with id ${note.id} as PENDING UPDATE")
        dao.updateNote(note = note.toLocalEntity(NoteOperationStatus.PendingUpdate))

        Log.d("Elijah", "updating remote note with id ${note.id}")
        remoteDataSource.updateNote(note.toRemoteEntity())

        Log.d("Elijah", "setting note with id ${note.id} back to idle")
        dao.setNoteOperationStatus(note.id, NoteOperationStatus.Idle.name)

        return true
    }

    override suspend fun deleteNote(id: String): Boolean {
        dao.setNoteOperationStatus(id, NoteOperationStatus.PendingDelete.name)

        remoteDataSource.deleteNote(id)

        dao.deleteNoteById(id)

        return true
    }

    override suspend fun createNote(note: Note): Boolean {
        Log.d("Elijah", "setting note with id ${note.id} as PENDING CREATE")
        dao.updateNote(note = note.toLocalEntity(NoteOperationStatus.PendingCreate))

        Log.d("Elijah", "creating remote note with id ${note.id}")
        remoteDataSource.createNote(note.toRemoteEntity())

        Log.d("Elijah", "setting note with id ${note.id} back to idle")
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
