package com.example.project.di

import android.content.Context
import androidx.room.Room
import com.example.project.data.local.dao.*
import com.example.project.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "filecast_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideFileDao(db: AppDatabase): FileDao = db.fileDao()
    @Provides fun provideConversionJobDao(db: AppDatabase): ConversionJobDao = db.conversionJobDao()
    @Provides fun provideFormatDao(db: AppDatabase): FormatDao = db.formatDao()
    @Provides fun provideFileTagDao(db: AppDatabase): FileTagDao = db.fileTagDao()
}
