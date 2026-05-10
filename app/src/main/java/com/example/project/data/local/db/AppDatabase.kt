package com.example.project.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.project.data.local.dao.*
import com.example.project.data.local.entity.*
import com.example.project.data.local.util.Converters

@Database(
    entities = [
        UserEntity::class,
        FileEntity::class,
        ConversionJobEntity::class,
        FormatEntity::class,
        FileTagEntity::class,
        FileTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fileDao(): FileDao
    abstract fun conversionJobDao(): ConversionJobDao
    abstract fun formatDao(): FormatDao
    abstract fun fileTagDao(): FileTagDao
}
