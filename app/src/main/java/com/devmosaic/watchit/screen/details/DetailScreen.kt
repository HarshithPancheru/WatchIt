package com.devmosaic.watchit.screen.details

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.devmosaic.watchit.model.MovieDetail
import com.devmosaic.watchit.network.CastMember


// Add this Factory class inside your DetailScreen.kt file
class DetailViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    movieId: String?,
    // We update how the ViewModel is created
) {
    val context = LocalContext.current
    val factory = DetailViewModelFactory(context.applicationContext as Application)
    val viewModel: DetailViewModel = viewModel(factory = factory)
    // Trigger the API call once when the screen is first composed
    LaunchedEffect(Unit) {
        // Ensure movieId is not null before trying to fetch
        if (movieId != null) {
            viewModel.fetchMovieDetails(movieId.toInt())
        }
    }

    // Observe the entire UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Title changes based on state
                    val titleText = when (val state = uiState) {
                        is DetailUiState.Success -> state.details.title
                        is DetailUiState.Loading -> "Loading..."
                        is DetailUiState.Error -> "Error"
                    }
                    Text(titleText)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Use a 'when' expression to display the correct UI for the current state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is DetailUiState.Success -> {
                    DetailContent(
                        details = state.details,
                        director = state.director, // Pass the new data
                        cast = state.cast,         // Pass the new data
                        onLike = { viewModel.onLikeClicked() },
                        onDislike = { viewModel.onDislikeClicked() }
                    )
                }
                is DetailUiState.Error -> {
                    Text(
                        text = "Could not load movie details.\nPlease try again later.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    details: MovieDetail,
    director: String?, // Add director
    cast: List<CastMember>, // Add cast
    onLike: () -> Unit,
    onDislike: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${details.posterPath}",
            contentDescription = details.title,
            modifier = Modifier
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = details.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Genres
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(details.genres) { genre ->
                AssistChip(onClick = { /* Not used */ }, label = { Text(genre.name) })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display runtime and release date
        Text(
            text = "Runtime: ${details.runtime} min | Released: ${details.releaseDate}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Display Director
        director?.let {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Director: ") }
                    append(it)
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        //plot
        Text(
            text = details.plot,
            style = MaterialTheme.typography.bodyLarge
        )



        Spacer(modifier = Modifier.height(24.dp))


        // Display Cast
        Text("Starring", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cast) { member ->
                AssistChip(onClick = { /* No action */ }, label = { Text(member.name) })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))



        // Like and Dislike Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally)
        ) {
            IconButton(onClick = onLike, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ThumbUp, contentDescription = "Like", tint = Color.Green)
            }
            IconButton(onClick = onDislike, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ThumbDown, contentDescription = "Dislike", tint = Color.Red)
            }
        }
    }
}