package com.example.project.repository

import com.example.project.data.local.dao.UserDao
import com.example.project.model.User
import com.example.project.repository.mappers.UserMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> =
        userDao.getAll().map { list -> list.map { UserMapper.toDomain(it) } }

    override suspend fun getUserById(id: String): User? =
        userDao.getById(id)?.let { UserMapper.toDomain(it) }

    override suspend fun insertUser(user: User) =
        userDao.insert(UserMapper.toEntity(user))

    override suspend fun updateUser(user: User) =
        userDao.update(UserMapper.toEntity(user))

    override suspend fun deleteUser(user: User) =
        userDao.delete(UserMapper.toEntity(user))
}
