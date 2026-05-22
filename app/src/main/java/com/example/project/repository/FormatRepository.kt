package com.example.project.repository

import com.example.project.data.local.entity.FormatEntity
import kotlinx.coroutines.flow.Flow

interface FormatRepository {
    fun getAllFormats(): Flow<List<FormatEntity>>
    suspend fun getFormatByName(name: String): FormatEntity?
    suspend fun insertFormat(format: FormatEntity)
    suspend fun insertAll(formats: List<FormatEntity>)
    suspend fun deleteFormat(format: FormatEntity)
}
