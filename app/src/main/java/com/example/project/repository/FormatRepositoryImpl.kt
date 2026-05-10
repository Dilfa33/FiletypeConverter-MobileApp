package com.example.project.repository

import com.example.project.data.local.dao.FormatDao
import com.example.project.data.local.entity.FormatEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FormatRepositoryImpl @Inject constructor(
    private val formatDao: FormatDao
) : FormatRepository {

    override fun getAllFormats(): Flow<List<FormatEntity>> =
        formatDao.getAll()

    override suspend fun getFormatByName(name: String): FormatEntity? =
        formatDao.getByName(name)

    override suspend fun insertFormat(format: FormatEntity) =
        formatDao.insert(format)

    override suspend fun insertAll(formats: List<FormatEntity>) =
        formatDao.insertAll(formats)

    override suspend fun deleteFormat(format: FormatEntity) =
        formatDao.delete(format)
}
