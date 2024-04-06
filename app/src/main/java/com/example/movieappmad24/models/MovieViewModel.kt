package com.example.movieappmad24.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class MovieViewModel : ViewModel() {
    private val _movies = getMovies().toMutableStateList()
    val movies : List<Movie>
        get() = _movies

    val favoriteMovies : List<Movie>
        get() = _movies.filter { movie -> movie.isFavorite }

    fun toggleFavorite(movieId: String) = _movies.find { it.id == movieId }?.let { movie ->
        movie.isFavorite = !movie.isFavorite
    }
}