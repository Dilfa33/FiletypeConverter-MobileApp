package com.example.project.data.local.dao

import androidx.room.*
import com.example.project.data.local.entity.ConversionJobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionJobDao {

    @Query("SELECT * FROM conversion_jobs ORDER BY startedAt DESC")
    fun getAll(): Flow<List<ConversionJobEntity>>

    @Query("SELECT * FROM conversion_jobs WHERE fileId = :fileId")
    fun getByFileId(fileId: String): Flow<List<ConversionJobEntity>>

    @Query("SELECT * FROM conversion_jobs WHERE id = :id")
    suspend fun getById(id: Int): ConversionJobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: ConversionJobEntity): Long

    @Update
    suspend fun update(job: ConversionJobEntity)

    @Delete
    suspend fun delete(job: ConversionJobEntity)
}
