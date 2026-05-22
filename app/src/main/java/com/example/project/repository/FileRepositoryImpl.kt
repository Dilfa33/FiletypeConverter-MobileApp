package com.example.project.repository

import com.example.project.data.local.dao.FileDao
import com.example.project.model.FileItem
import com.example.project.repository.mappers.FileMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val fileDao: FileDao
) : FileRepository {

    override fun getAllFiles(): Flow<List<FileItem>> =
        fileDao.getAll().map { list -> list.map { FileMapper.toDomain(it) } }

    override fun getFilesByUser(userId: String): Flow<List<FileItem>> =
        fileDao.getByUser(userId).map { list -> list.map { FileMapper.toDomain(it) } }

    override suspend fun getFileById(id: String): FileItem? =
        fileDao.getById(id)?.let { FileMapper.toDomain(it) }

    override suspend fun insertFile(file: FileItem, userId: String) =
        fileDao.insert(FileMapper.toEntity(file, userId))

    override suspend fun updateFile(file: FileItem, userId: String) =
        fileDao.update(FileMapper.toEntity(file, userId))

    override suspend fun deleteFile(id: String) =
        fileDao.deleteById(id)

    override suspend fun deleteAllByUser(userId: String) =
        fileDao.deleteAllByUser(userId)
}
