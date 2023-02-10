package com.dangerfield.core.notes

import com.dangerfield.core.notes.local.LocalNoteEntity
import com.dangerfield.core.notes.local.NoteOperationStatus
import com.dangerfield.core.notes.remote.RemoteNoteEntity
import com.dangerfield.core.notesapi.Note
import javax.inject.Inject

class NotesConflictResolver @Inject constructor() {

    @Suppress("UnusedPrivateMember")
    suspend fun resolve(
        localNotes: List<LocalNoteEntity>,
        pendingUpdates: List<LocalNoteEntity>,
        pendingDeletes: List<LocalNoteEntity>,
        pendingCreates: List<LocalNoteEntity>,
        remoteNotes: List<RemoteNoteEntity>
    ): List<Note> {
        /*
        operations: update, delete, create

        collisions:
        update, update
        delete, update

        events:
        external create
        external delete
        external update
         */

        // favor offline only
        return localNotes.filter { it.operationStatus == NoteOperationStatus.Idle.name }.map { it.toDomainNote() }
    }
}
