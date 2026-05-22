package com.example.project.di

import com.example.project.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindFileRepository(impl: FileRepositoryImpl): FileRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindConversionJobRepository(impl: ConversionJobRepositoryImpl): ConversionJobRepository

    @Binds @Singleton
    abstract fun bindFormatRepository(impl: FormatRepositoryImpl): FormatRepository
}
