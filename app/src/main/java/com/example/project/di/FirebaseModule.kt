package com.example.project.di

import com.example.project.repository.AuthRepository
import com.example.project.repository.FirebaseAuthRepositoryImpl
import com.example.project.repository.FirestoreConversionRepository
import com.example.project.repository.FirestoreConversionRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository =
        FirebaseAuthRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideFirestoreConversionRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): FirestoreConversionRepository =
        FirestoreConversionRepositoryImpl(firestore, firebaseAuth)
}
