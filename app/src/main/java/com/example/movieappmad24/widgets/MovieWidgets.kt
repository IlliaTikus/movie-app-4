package com.example.movieappmad24.widgets

import android.content.Context
import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Looper.prepare
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.movieappmad24.models.Movie
import com.example.movieappmad24.models.getMovies
import com.example.movieappmad24.navigation.Screen
import com.example.movieappmad24.models.MovieViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer


@Composable
fun MovieList(
    modifier: Modifier,
    movieViewModel: MovieViewModel,
    navController: NavController,
    movies: List<Movie>
){
    LazyColumn(modifier = modifier) {
        items(movies) { movie ->
            MovieRow(
                modifier = Modifier,
                movie = movie,
                movieViewModel = movieViewModel
            ) { movieId ->
                navController.navigate(route = Screen.DetailScreen.withId(movieId))
            }
        }
    }
}

@Composable
fun MovieRow(
    modifier: Modifier = Modifier,
    movie: Movie,
    movieViewModel: MovieViewModel,
    onItemClick: (String) -> Unit = {}
){
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(5.dp),
        shape = ShapeDefaults.Large,
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column {
            MovieCardHeader(imageUrl = movie.images[0], isFavorite = movie.isFavorite, onFavoriteClick = { movieViewModel.toggleFavorite(movie.id) }, onItemClick = { onItemClick(movie.id) })
            MovieDetails(modifier = modifier.padding(12.dp), movie = movie, onItemClick = { onItemClick(movie.id) })
        }
    }
}

@Composable
fun MovieCardHeader(imageUrl: String, isFavorite: Boolean, onFavoriteClick: () -> Unit, onItemClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        MovieImage(imageUrl, onItemClick)
        FavoriteIcon(isFavorite = isFavorite, onClick = onFavoriteClick)
    }
}

@Composable
fun MovieImage(imageUrl: String, onItemClick: () -> Unit){
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = "movie poster",
        loading = {
            CircularProgressIndicator()
        },
        modifier = Modifier.clickable(onClick = onItemClick)
    )
}

@Composable
fun FavoriteIcon(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.TopEnd
    ){
        Icon(
            tint = MaterialTheme.colorScheme.secondary,
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Add to favorites",
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}


@Composable
fun MovieDetails(modifier: Modifier, movie: Movie, onItemClick: () -> Unit) {
    var showDetails by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = movie.title)
        Icon(modifier = Modifier
            .clickable {
                showDetails = !showDetails
            },
            imageVector =
            if (showDetails) Icons.Filled.KeyboardArrowDown
            else Icons.Default.KeyboardArrowUp, contentDescription = "show more")
    }


    AnimatedVisibility(
        visible = showDetails,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column (modifier = modifier) {
            Text(text = "Director: ${movie.director}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Released: ${movie.year}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Genre: ${movie.genre}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Actors: ${movie.actors}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Rating: ${movie.rating}", style = MaterialTheme.typography.bodySmall)

            Divider(modifier = Modifier.padding(3.dp))

            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.DarkGray, fontSize = 13.sp)) {
                    append("Plot: ")
                }
                withStyle(style = SpanStyle(color = Color.DarkGray, fontSize = 13.sp, fontWeight = FontWeight.Normal)){
                    append(movie.plot)
                }
            })
        }
    }
}


@Composable
fun HorizontalScrollableImageView(movie: Movie) {
    LazyRow {
        items(movie.images) { image ->
            Card(
                modifier = Modifier
                    .padding(12.dp)
                    .size(240.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Movie poster",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun VideoPlayer(context: Context, videoResId: Int) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse("android.resource://${context.packageName}/$videoResId"))
            setMediaItem(mediaItem)
            prepare()
        }
    }

    AndroidView({ PlayerView(it).apply { player = exoPlayer } }, modifier = Modifier.fillMaxWidth())

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                exoPlayer.playWhenReady = true
            }

            override fun onStop(owner: LifecycleOwner) {
                exoPlayer.playWhenReady = false
            }
        }

        lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
}