package com.dangerfield.notable

import android.content.Context
import androidx.room.Room
import com.dangerfield.core.notes.OfflineOnlyNoteRepository
import com.dangerfield.core.notes.local.NotableDatabase
import com.dangerfield.core.notes.local.NotesDao
import com.dangerfield.core.notes.remote.FirebaseNotesDataSource
import com.dangerfield.core.notes.remote.NotesRemoteDataSource
import com.dangerfield.core.notesapi.NoteRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton

@Module(includes = [ApplicationModule.Bindings::class])
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun providesNotableDatabase(@ApplicationContext context: Context): NotableDatabase {
        return Room.databaseBuilder(context, NotableDatabase::class.java, "notable.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun providesArticleDao(db: NotableDatabase): NotesDao {
        return db.noteDao()
    }

    @Module
    @DisableInstallInCheck
    interface Bindings {

        @Binds
        fun bindNotesRepository(impl: OfflineOnlyNoteRepository): NoteRepository

        @Binds
        fun bindRemoteNoteDatasource(impl: FirebaseNotesDataSource): NotesRemoteDataSource
    }
}
