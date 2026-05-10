package com.example.project.repository

import com.example.project.data.local.dao.ConversionJobDao
import com.example.project.data.local.entity.ConversionJobEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversionJobRepositoryImpl @Inject constructor(
    private val conversionJobDao: ConversionJobDao
) : ConversionJobRepository {

    override fun getAllJobs(): Flow<List<ConversionJobEntity>> =
        conversionJobDao.getAll()

    override fun getJobsByFile(fileId: String): Flow<List<ConversionJobEntity>> =
        conversionJobDao.getByFileId(fileId)

    override suspend fun insertJob(job: ConversionJobEntity): Long =
        conversionJobDao.insert(job)

    override suspend fun updateJob(job: ConversionJobEntity) =
        conversionJobDao.update(job)

    override suspend fun deleteJob(job: ConversionJobEntity) =
        conversionJobDao.delete(job)
}
