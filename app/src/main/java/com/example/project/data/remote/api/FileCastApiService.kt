package com.example.project.data.remote.api

import com.example.project.data.remote.dto.ConversionDto
import com.example.project.data.remote.dto.CreateConversionDto
import com.example.project.data.remote.dto.UpdateConversionDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FileCastApiService {

    @GET("conversions")
    suspend fun getConversions(
        @Header("X-Authentication") authHeader: String = "yes"
    ): List<ConversionDto>

    @GET("conversions/{id}")
    suspend fun getConversionById(
        @Path("id") id: String,
        @Header("X-Authentication") authHeader: String = "yes"
    ): ConversionDto

    @POST("conversions")
    suspend fun createConversion(
        @Body conversion: CreateConversionDto,
        @Header("X-Authentication") authHeader: String = "yes"
    ): ConversionDto

    @PUT("conversions/{id}")
    suspend fun updateConversion(
        @Path("id") id: String,
        @Body conversion: UpdateConversionDto,
        @Header("X-Authentication") authHeader: String = "yes"
    ): ConversionDto

    @DELETE("conversions/{id}")
    suspend fun deleteConversion(
        @Path("id") id: String,
        @Header("X-Authentication") authHeader: String = "yes"
    )
}
