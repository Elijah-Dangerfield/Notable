package com.dangerfield.core.notes.remote

import com.dangerfield.core.notes.remote.FirebaseConstants.DEBUG_USER_ID
import com.dangerfield.core.notes.remote.FirebaseConstants.USERS_COLLECTION_KEY
import com.dangerfield.core.notes.remote.FirebaseConstants.USER_NOTES_SUB_COLLECTION_KEY
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseNotesDataSource @Inject constructor(
    private val firebaseDb: FirebaseFirestore,
) : NotesRemoteDataSource {
    override suspend fun deleteNote(id: String) {
        firebaseDb
            .collection(USERS_COLLECTION_KEY)
            .document(DEBUG_USER_ID)
            .collection(USER_NOTES_SUB_COLLECTION_KEY)
            .document(id)
            .delete()
            .await()
    }

    override suspend fun updateNote(note: RemoteNoteEntity) {
        firebaseDb
            .collection(USERS_COLLECTION_KEY)
            .document(DEBUG_USER_ID)
            .collection(USER_NOTES_SUB_COLLECTION_KEY)
            .document(note.id)
            .set(note)
            .await()
    }

    override suspend fun createNote(note: RemoteNoteEntity) {
        firebaseDb
            .collection(USERS_COLLECTION_KEY)
            .document(DEBUG_USER_ID)
            .collection(USER_NOTES_SUB_COLLECTION_KEY)
            .document(note.id)
            .set(note)
            .await()
    }

    override suspend fun getNotes(): List<RemoteNoteEntity> {
        return firebaseDb
            .collection(USERS_COLLECTION_KEY)
            .document(DEBUG_USER_ID)
            .collection(USER_NOTES_SUB_COLLECTION_KEY)
            .get(Source.SERVER)
            .await()
            .toObjects(RemoteNoteEntity::class.java)
    }
}
