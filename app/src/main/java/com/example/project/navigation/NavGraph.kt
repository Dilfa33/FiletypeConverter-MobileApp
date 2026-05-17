package com.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project.ui.screens.details.FileDetailsScreen
import com.example.project.ui.screens.history.HistoryScreen
import com.example.project.ui.screens.login.LoginScreen
import com.example.project.ui.screens.profile.ProfileScreen
import com.example.project.ui.screens.register.RegisterScreen
import com.example.project.ui.screens.result.ConversionResultScreen
import com.example.project.ui.screens.upload.UploadScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN,
        modifier = modifier
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(AppRoutes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(BottomNavItem.Upload.route) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(BottomNavItem.Upload.route) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(BottomNavItem.History.route) {
            HistoryScreen(
                onFileClick = { fileId, fileName ->
                    navController.navigate(AppRoutes.fileDetails(fileId, fileName))
                }
            )
        }
        composable(BottomNavItem.Upload.route) {
            UploadScreen(
                onConversionComplete = { fileId, fileName ->
                    navController.navigate(AppRoutes.conversionResult(fileId, fileName))
                }
            )
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
        composable(AppRoutes.CONVERSION_RESULT) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName").orEmpty()
            ConversionResultScreen(
                fileName = fileName,
                onViewDetails = {
                    val fileId = backStackEntry.arguments?.getString("fileId").orEmpty()
                    navController.navigate(AppRoutes.fileDetails(fileId, fileName))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.FILE_DETAILS) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName").orEmpty()
            FileDetailsScreen(
                fileName = fileName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
