package com.devmosaic.watchit.screen.home

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devmosaic.watchit.model.Movie
import com.devmosaic.watchit.navigation.MovieScreens
import com.devmosaic.watchit.widgets.MovieRow

class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun MainContent(
    navController: NavController,
    movieList: List<Movie>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState) {
        items(items = movieList) { movie ->
            MovieRow(movie = movie) { movieId ->
                navController.navigate(route = MovieScreens.DetailesScreen.name + "/$movieId")
            }
        }

        // Loading indicator at the bottom
        if (isLoadingMore) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // This effect triggers 'onLoadMore' when the last item is visible
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItemsCount = layoutInfo.totalItemsCount
                if (totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 5) {
                    onLoadMore()
                }
            }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val factory = HomeViewModelFactory(LocalContext.current.applicationContext as Application)
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        PreferencesDialog(
            scores = uiState.preferenceScores,
            onDismiss = { showDialog = false },
            onReset = { viewModel.resetUserPreferences() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WatchIt", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(color = 0xFF273248)),
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Person, contentDescription = "User Preferences", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshFeed() }
        ) {
            Column {
                uiState.recommendationReason?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
                MainContent(
                    navController = navController,
                    movieList = uiState.movies,
                    isLoadingMore = uiState.isLoadingMore,
                    onLoadMore = { viewModel.loadMoreMovies() }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesDialog(
    scores: Map<String, Int>,
    onDismiss: () -> Unit,
    onReset: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // --- DIALOG HEADER ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Preferences Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Your Genre Preferences", style = MaterialTheme.typography.headlineSmall)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // --- DIALOG CONTENT ---
                if (scores.isEmpty()) {
                    // Empty State UI
                    Column(
                        modifier = Modifier.padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.Gray)
                        Text(
                            "No preferences saved yet. Like or dislike movies to build your profile!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    // Scores List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(scores.toList()) { (genreName, score) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(genreName, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.weight(1f))
                                Badge(
                                    containerColor = if (score > 0) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                                    contentColor = if (score > 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                ) {
                                    Text(
                                        text = if (score > 0) "+$score" else score.toString(),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // --- DIALOG ACTIONS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        onReset()
                        onDismiss() // Close dialog after resetting
                    }) {
                        Text("Reset")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}