package com.dangerfield.core.notes

import com.dangerfield.core.notes.local.LocalNoteEntity
import com.dangerfield.core.notes.local.NoteOperationStatus
import com.dangerfield.core.notes.remote.RemoteNoteEntity
import com.dangerfield.core.notesapi.Note

fun LocalNoteEntity.toDomainNote(): Note = Note(
    title = this.title,
    content = this.content,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    color = this.color,
    id = this.id,
)

fun Note.toLocalEntity(operationStatus: NoteOperationStatus): LocalNoteEntity = LocalNoteEntity(
    title = this.title,
    content = this.content,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    color = this.color,
    operationStatus = operationStatus.name,
    id = this.id,
)

fun RemoteNoteEntity.toDomainNote(): Note = Note(
    title = this.title,
    content = this.content,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    color = this.color,
    id = this.id,
)

fun Note.toRemoteEntity(): RemoteNoteEntity = RemoteNoteEntity(
    title = this.title,
    content = this.content,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    color = this.color,
    id = this.id,
)
