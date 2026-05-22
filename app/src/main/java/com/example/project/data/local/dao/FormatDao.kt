package com.example.project.data.local.dao

import androidx.room.*
import com.example.project.data.local.entity.FormatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormatDao {

    @Query("SELECT * FROM formats ORDER BY name ASC")
    fun getAll(): Flow<List<FormatEntity>>

    @Query("SELECT * FROM formats WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): FormatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(format: FormatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(formats: List<FormatEntity>)

    @Delete
    suspend fun delete(format: FormatEntity)
}
