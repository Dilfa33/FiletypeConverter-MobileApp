package com.example.project.data.local.util

import com.example.project.data.local.entity.FormatEntity
import com.example.project.di.SessionManager
import com.example.project.model.HardcodedData
import com.example.project.model.User
import com.example.project.repository.FileRepository
import com.example.project.repository.FormatRepository
import com.example.project.repository.UserRepository
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository,
    private val formatRepository: FormatRepository,
    private val sessionManager: SessionManager
) {
    suspend fun seedIfEmpty() {
        val users = userRepository.getAllUsers().first()
        if (users.isNotEmpty()) {
            sessionManager.currentUserId = users.first().id
            return
        }

        val userId = "user-1"
        userRepository.insertUser(
            User(
                id           = userId,
                username     = "John Doe",
                email        = "john@example.com",
                passwordHash = "password123".sha256()
            )
        )
        sessionManager.currentUserId = userId

        val formatCategories = mapOf(
            "PDF" to "document", "DOCX" to "document", "TXT" to "document",
            "XLSX" to "document", "PNG" to "image", "JPG" to "image",
            "MP4" to "video", "MP3" to "audio", "WAV" to "audio", "ZIP" to "archive"
        )
        formatRepository.insertAll(
            HardcodedData.supportedFormats.map { name ->
                FormatEntity(
                    name      = name,
                    extension = ".${name.lowercase()}",
                    category  = formatCategories[name] ?: "other"
                )
            }
        )

        HardcodedData.files.forEach { file ->
            fileRepository.insertFile(file, userId)
        }
    }
}

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(toByteArray()).joinToString("") { "%02x".format(it) }
}
