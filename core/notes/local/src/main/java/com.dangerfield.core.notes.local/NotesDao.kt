package com.dangerfield.core.notes.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM NOTES")
    fun getNotes(): Flow<List<LocalNoteEntity>>

    @Query(" SELECT * FROM NOTES WHERE id = :id")
    suspend fun getNoteById(id: String): LocalNoteEntity?

    @Query("SELECT * FROM NOTES WHERE operationStatus = :operationStatus")
    fun getNotesWithOperationStatus(operationStatus: String): List<LocalNoteEntity>

    @Query("UPDATE NOTES SET operationStatus = :operationStatus WHERE id = :id")
    suspend fun setNoteOperationStatus(id: String, operationStatus: String)

    @Query("DELETE FROM NOTES WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: LocalNoteEntity)

    @Query("DELETE FROM NOTES")
    fun deleteNotes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<LocalNoteEntity>)

    @Transaction
    suspend fun replaceNotes(notes: List<LocalNoteEntity>) {
        deleteNotes()
        insertNotes(notes)
    }
}
