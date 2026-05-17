package com.example.project.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.project.data.local.dao.*
import com.example.project.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        FileEntity::class,
        ConversionJobEntity::class,
        FormatEntity::class,
        FileTagEntity::class,
        FileTagCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fileDao(): FileDao
    abstract fun conversionJobDao(): ConversionJobDao
    abstract fun formatDao(): FormatDao
    abstract fun fileTagDao(): FileTagDao
}
