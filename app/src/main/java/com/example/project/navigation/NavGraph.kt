package com.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project.ui.screens.details.FileDetailsScreen
import com.example.project.ui.screens.history.HistoryScreen
import com.example.project.ui.screens.profile.ProfileScreen
import com.example.project.ui.screens.result.ConversionResultScreen
import com.example.project.ui.screens.upload.UploadScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Upload.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.History.route) {
            HistoryScreen(
                onFileClick = { fileId -> navController.navigate(AppRoutes.fileDetails(fileId)) }
            )
        }
        composable(BottomNavItem.Upload.route) {
            UploadScreen(
                onConversionComplete = { fileId -> navController.navigate(AppRoutes.conversionResult(fileId)) }
            )
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
        composable(AppRoutes.CONVERSION_RESULT) { backStackEntry ->
            val fileId = backStackEntry.arguments?.getString("fileId").orEmpty()
            ConversionResultScreen(
                fileId = fileId,
                onViewDetails = { navController.navigate(AppRoutes.fileDetails(fileId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.FILE_DETAILS) { backStackEntry ->
            val fileId = backStackEntry.arguments?.getString("fileId").orEmpty()
            FileDetailsScreen(
                fileId = fileId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
