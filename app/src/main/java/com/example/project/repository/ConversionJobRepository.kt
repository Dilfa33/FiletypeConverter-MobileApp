package com.example.project.repository

import com.example.project.data.local.entity.ConversionJobEntity
import kotlinx.coroutines.flow.Flow

interface ConversionJobRepository {
    fun getAllJobs(): Flow<List<ConversionJobEntity>>
    fun getJobsByFile(fileId: String): Flow<List<ConversionJobEntity>>
    suspend fun insertJob(job: ConversionJobEntity): Long
    suspend fun updateJob(job: ConversionJobEntity)
    suspend fun deleteJob(job: ConversionJobEntity)
}
