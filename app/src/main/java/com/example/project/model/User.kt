package com.example.project.model

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val totalConversions: Int = 0
)
