package com.devmosaic.watchit.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devmosaic.watchit.model.Movie
import com.devmosaic.watchit.model.getMovies
import com.devmosaic.watchit.navigation.MovieScreens
import com.devmosaic.watchit.widgets.MovieRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Magenta),
                title = {
                    Text("Movies")
                }
            )
        }
    ) {
            innerPadding ->
        MainContent(navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun MainContent(navController: NavController,modifier: Modifier = Modifier,
                movieList: List<Movie> = getMovies()){
    Column(
        modifier = modifier.padding(14.dp)
    ) {
        LazyColumn {
            items(
                items = movieList
            ){
                MovieRow(movie = it){
                        movie->
                    navController.navigate(route = MovieScreens.DetailesScreen.name+"/$movie")
                }
            }
        }
    }
}

