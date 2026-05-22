package com.example.project.di

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    var currentUserId: String = "user-1"
    var notificationsEnabled: Boolean = true
}
