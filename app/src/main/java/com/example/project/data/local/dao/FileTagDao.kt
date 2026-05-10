package com.example.project.data.local.dao

import androidx.room.*
import com.example.project.data.local.entity.FileTagCrossRef
import com.example.project.data.local.entity.FileTagEntity
import com.example.project.data.local.entity.FileWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface FileTagDao {

    @Query("SELECT * FROM file_tags ORDER BY name ASC")
    fun getAll(): Flow<List<FileTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: FileTagEntity): Long

    @Delete
    suspend fun delete(tag: FileTagEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: FileTagCrossRef)

    @Delete
    suspend fun deleteCrossRef(crossRef: FileTagCrossRef)

    @Transaction
    @Query("SELECT * FROM files")
    fun getAllFilesWithTags(): Flow<List<FileWithTags>>
}
