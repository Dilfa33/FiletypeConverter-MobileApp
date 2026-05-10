package com.example.project.repository

import com.example.project.model.FileItem
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    fun getAllFiles(): Flow<List<FileItem>>
    fun getFilesByUser(userId: String): Flow<List<FileItem>>
    suspend fun getFileById(id: String): FileItem?
    suspend fun insertFile(file: FileItem, userId: String)
    suspend fun updateFile(file: FileItem, userId: String)
    suspend fun deleteFile(id: String)
}
