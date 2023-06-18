package com.vinayismd.contactify

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vinayismd.contactify.screens.ContactScreen
import com.vinayismd.contactify.screens.InfoScreen
import com.vinayismd.contactify.screens.MainScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewmodel: MainActivityVM = hiltViewModel()
    NavHost(navController = navController, startDestination = Screens.MainScreen.route) {
        composable(route = Screens.MainScreen.route) {
            MainScreen(navController, viewmodel)
        }

        composable(route = Screens.ContactScreen.route) {
             ContactScreen(navController, viewmodel)
        }
        composable(route = Screens.InfoScreen.route) {
            InfoScreen(navController, viewmodel)
        }
    }
}






