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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Emulator → 10.0.2.2 maps to your PC's localhost
    // Physical device on same Wi-Fi → replace with your PC's LAN IP, e.g. http://192.168.1.5:8000/
    private const val BASE_URL = "http://10.0.2.2:8000/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(5, TimeUnit.SECONDS)   // Fail fast if backend is offline
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
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
