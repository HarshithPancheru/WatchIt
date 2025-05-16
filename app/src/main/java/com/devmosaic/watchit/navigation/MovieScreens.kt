package com.devmosaic.watchit.navigation

enum class MovieScreens{
    HomeScreen,
    DetailesScreen;

    companion object{
        fun fromRoute(route: String?):MovieScreens
        = when (route?.substringBefore("/")){
            HomeScreen.name -> HomeScreen
            DetailesScreen.name -> DetailesScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}