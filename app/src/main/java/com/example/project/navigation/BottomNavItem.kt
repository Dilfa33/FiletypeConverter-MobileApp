package com.example.project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object History : BottomNavItem("history", "History", Icons.Default.History)
    object Upload : BottomNavItem("upload", "Convert", Icons.Default.Upload)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

object AppRoutes {
    const val CONVERSION_RESULT = "result/{fileId}"
    const val FILE_DETAILS = "details/{fileId}"

    fun conversionResult(fileId: String) = "result/$fileId"
    fun fileDetails(fileId: String) = "details/$fileId"
}
