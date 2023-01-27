package com.dangerfield.core.notes

import com.dangerfield.core.notes.local.LocalNoteEntity
import com.dangerfield.core.notes.remote.RemoteNoteEntity
import com.dangerfield.core.notesapi.Note
import javax.inject.Inject

class NotesConflictResolver @Inject constructor() {

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

        return remoteNotes.map { it.toDomainNote() }
    }
}
