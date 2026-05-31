package com.example.project.repository

import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreConversionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : FirestoreConversionRepository {

    private val collection = firestore.collection("conversions")

    // Realtime updates — snapshot listener wrapped in callbackFlow
    override fun observeConversions(userId: String): Flow<List<FileItem>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    runCatching {
                        FileItem(
                            id             = doc.id,
                            name           = doc.getString("name") ?: "",
                            originalFormat = doc.getString("originalFormat") ?: "",
                            targetFormat   = doc.getString("targetFormat") ?: "",
                            sizeMb         = doc.getDouble("sizeMb")?.toFloat() ?: 0f,
                            date           = doc.getString("date") ?: "",
                            status         = ConversionStatus.valueOf(
                                doc.getString("status") ?: "SUCCESS"
                            ),
                            outputPath     = doc.getString("outputPath")
                        )
                    }.getOrNull()
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addConversion(file: FileItem, userId: String) {
        val data = hashMapOf(
            "userId"         to userId,
            "name"           to file.name,
            "originalFormat" to file.originalFormat,
            "targetFormat"   to file.targetFormat,
            "sizeMb"         to file.sizeMb,
            "date"           to file.date,
            "status"         to file.status.name,
            "outputPath"     to (file.outputPath ?: "")
        )
        collection.add(data).await()
    }

    override suspend fun deleteConversion(docId: String) {
        collection.document(docId).delete().await()
    }
}
