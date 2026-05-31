package com.example.project.repository

import com.example.project.data.remote.api.FileCastApiService
import com.example.project.data.remote.dto.ConversionDto
import com.example.project.data.remote.dto.ConvertResponseDto
import com.example.project.data.remote.dto.CreateConversionDto
import com.example.project.data.remote.dto.UpdateConversionDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class NetworkConversionRepositoryImpl @Inject constructor(
    private val api: FileCastApiService
) : NetworkConversionRepository {

    override suspend fun getConversions(): List<ConversionDto> = api.getConversions()

    override suspend fun getConversionById(id: String): ConversionDto = api.getConversionById(id)

    override suspend fun createConversion(dto: CreateConversionDto): ConversionDto =
        api.createConversion(dto)

    override suspend fun updateConversion(id: String, dto: UpdateConversionDto): ConversionDto =
        api.updateConversion(id, dto)

    override suspend fun deleteConversion(id: String) = api.deleteConversion(id)

    override suspend fun convertFile(
        fileBytes: ByteArray,
        fileName: String,
        targetFormat: String
    ): ConvertResponseDto {
        val requestFile = fileBytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        val filePart    = MultipartBody.Part.createFormData("file", fileName, requestFile)
        val formatBody  = targetFormat.toRequestBody("text/plain".toMediaTypeOrNull())
        return api.convertFile(filePart, formatBody)
    }
}
