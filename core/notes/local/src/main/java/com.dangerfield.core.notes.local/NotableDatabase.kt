package com.dangerfield.core.notes.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [LocalNoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotableDatabase : RoomDatabase() {

    abstract fun noteDao(): NotesDao

    companion object {
        @Volatile
        private var instance: NotableDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            NotableDatabase::class.java, "notable.db"
        )
            .build()
    }
}
