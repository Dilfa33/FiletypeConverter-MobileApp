package com.example.project.di

import com.example.project.data.remote.api.FileCastApiService
import com.example.project.repository.NetworkConversionRepository
import com.example.project.repository.NetworkConversionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Replace with your MockAPI.io URL, e.g.:
    // https://67abc123def.mockapi.io/api/v1/
    private const val BASE_URL = "https://682e3e34395f2df5b9cce9eb.mockapi.io/api/v1/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideFileCastApiService(retrofit: Retrofit): FileCastApiService =
        retrofit.create(FileCastApiService::class.java)

    @Provides
    @Singleton
    fun provideNetworkConversionRepository(
        api: FileCastApiService
    ): NetworkConversionRepository = NetworkConversionRepositoryImpl(api)
}
