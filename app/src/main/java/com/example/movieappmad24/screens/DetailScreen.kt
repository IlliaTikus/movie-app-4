package com.example.movieappmad24.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieappmad24.models.MovieViewModel
import com.example.movieappmad24.models.getMovies
import com.example.movieappmad24.widgets.HorizontalScrollableImageView
import com.example.movieappmad24.widgets.MovieRow
import com.example.movieappmad24.widgets.SimpleTopAppBar
import com.example.movieappmad24.widgets.VideoPlayer

@SuppressLint("DiscouragedApi")
@Composable
fun DetailScreen(
    movieId: String?,
    navController: NavController,
    movieViewModel: MovieViewModel
) {
    val context = LocalContext.current

    movieId?.let {
        val movie = movieViewModel.movies.find { it.id == movieId }

        movie?.let {
            val videoResId = context.resources.getIdentifier(movie.trailer, "raw", context.packageName)
            Scaffold (
                topBar = {
                    SimpleTopAppBar(title = movie.title) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    }
                }
            ){ innerPadding ->
                Column(modifier = Modifier.fillMaxSize()) {
                    MovieRow(modifier = Modifier.padding(innerPadding), movie = movie, movieViewModel = movieViewModel)
                    Text(text = "Movie Trailer", modifier = Modifier.padding(16.dp))
                    VideoPlayer(context, videoResId)
                    HorizontalScrollableImageView(movie = movie)
                }
            }
        }
    }
}