package com.example.project.data.local.dao

import androidx.room.*
import com.example.project.data.local.entity.FileEntity
import com.example.project.data.local.entity.FileWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Query("SELECT * FROM files ORDER BY convertedAt DESC")
    fun getAll(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE id = :id")
    suspend fun getById(id: String): FileEntity?

    @Query("SELECT * FROM files WHERE userId = :userId ORDER BY convertedAt DESC")
    fun getByUser(userId: String): Flow<List<FileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: FileEntity)

    @Update
    suspend fun update(file: FileEntity)

    @Delete
    suspend fun delete(file: FileEntity)

    @Query("DELETE FROM files WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM files WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)

    @Transaction
    @Query("SELECT * FROM files WHERE id = :id")
    suspend fun getFileWithTags(id: String): FileWithTags?
}
