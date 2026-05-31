package com.example.project.repository

import com.example.project.model.FileItem
import kotlinx.coroutines.flow.Flow

interface FirestoreConversionRepository {
    fun observeConversions(userId: String): Flow<List<FileItem>>
    suspend fun addConversion(file: FileItem, userId: String)
    suspend fun deleteConversion(docId: String)
}
