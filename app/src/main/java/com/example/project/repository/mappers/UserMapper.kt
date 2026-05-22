package com.example.project.repository.mappers

import com.example.project.data.local.entity.UserEntity
import com.example.project.model.User

object UserMapper {

    fun toDomain(entity: UserEntity): User = User(
        id           = entity.id,
        username     = entity.username,
        email        = entity.email,
        passwordHash = entity.passwordHash
    )

    fun toEntity(domain: User): UserEntity = UserEntity(
        id           = domain.id,
        username     = domain.username,
        email        = domain.email,
        passwordHash = domain.passwordHash
    )
}
