package com.devmosaic.watchit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devmosaic.watchit.screen.details.DetailScreen
//import com.devmosaic.watchit.screen.details. DetailScreen
import com.devmosaic.watchit.screen.home.HomeScreen

@Composable
fun MovieNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = MovieScreens.HomeScreen.name) {
        composable(MovieScreens.HomeScreen.name){
            //here we pass where this should lead us to
            HomeScreen(navController)
        }

        composable(MovieScreens.DetailesScreen.name+"/{movie}",
            arguments = listOf(navArgument(name = "movie") {type= NavType.StringType})){
            backStackEntry ->

            DetailScreen(navController, backStackEntry.arguments?.getString("movie"))
        }
    }
}