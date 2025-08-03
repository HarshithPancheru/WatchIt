package com.devmosaic.watchit.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.devmosaic.watchit.model.Movie

// Note: rememberImagePainter with CircleCropTransformation() is replaced by Coil's AsyncImage
// and a Modifier.clip(CircleShape) for a more modern approach.

@Composable
fun MovieRow(
    modifier: Modifier = Modifier,
    movie: Movie, // Use the new Movie data class
    onItemClick: (Int) -> Unit = {} // The ID is now an Int
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable {
                onItemClick(movie.id) // Pass the integer ID
            },
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            //verticalAlignment = Alignment.CenterVertically, // This helps align items better
            //horizontalArrangement = Arrangement.Start
        ) {
            // Using Coil's AsyncImage
            AsyncImage(
                model = imageUrl,
                contentDescription = "${movie.title} poster",
                modifier = Modifier
                    .padding(12.dp)
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)) {

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge
                )
                // The basic discovery endpoint doesn't have the director, so we remove it here.
                // We'll add it back on the Detail Screen.
                Text(
                    text = "Released: ${movie.releaseDate}",
                    style = MaterialTheme.typography.bodySmall
                )

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.DarkGray, fontSize = 13.sp)) {
                                    append("Plot: ")
                                }
                                withStyle(style = SpanStyle(color = Color.DarkGray, fontSize = 13.sp, fontWeight = FontWeight.Light)) {
                                    append(movie.plot) // Use 'plot' from the new model (mapped from 'overview')
                                }
                            },
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 3.dp))
                        Text(
                            text = "Rating: ${movie.voteAverage}", // Use voteAverage
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Icon(
                    imageVector = if (!expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Expand",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            expanded = !expanded
                        },
                    tint = Color.DarkGray
                )
            }
        }
    }
}