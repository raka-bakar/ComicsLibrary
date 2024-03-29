package com.catalin.comicslibrary

import android.content.Context
import androidx.room.Room
import com.catalin.comicslibrary.model.api.ApiService
import com.catalin.comicslibrary.model.api.MarvelApiRepo
import com.catalin.comicslibrary.model.connectivity.ConnectivityMonitor
import com.catalin.comicslibrary.model.db.CharacterDao
import com.catalin.comicslibrary.model.db.CollectionDb
import com.catalin.comicslibrary.model.db.CollectionDbRepo
import com.catalin.comicslibrary.model.db.CollectionDbRepoImpl
import com.catalin.comicslibrary.model.db.Constants.DB
import com.catalin.comicslibrary.model.db.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {
    @Provides
    fun provideApiRepo() = MarvelApiRepo(ApiService.api)

    @Provides
    fun provideCollectionDb(@ApplicationContext context: Context): CollectionDb =
        Room.databaseBuilder(
            context,
            CollectionDb::class.java,
            DB
        ).build()

    @Provides
    fun provideCharacterDao(collectionDb: CollectionDb): CharacterDao = collectionDb.characterDao()

    @Provides
    fun provideNoteDao(collectionDb: CollectionDb) = collectionDb.noteDao()

    @Provides
    fun provideDbRepoImpl(characterDao: CharacterDao, noteDao: NoteDao): CollectionDbRepo =
        CollectionDbRepoImpl(characterDao, noteDao)

    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context) =
        ConnectivityMonitor.getInstance(context)
}