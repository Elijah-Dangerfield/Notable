package com.dangerfield.core.notes.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocalNoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotableDatabase : RoomDatabase() {

    abstract fun noteDao(): NotesDao
}
