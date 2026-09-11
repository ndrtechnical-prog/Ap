package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val year: Int,
    val rating: Double,
    val duration: String,
    val quality: String = "1080p HD",
    val genre: String,
    val genres: String, // Comma separated e.g. "Action, Thriller, Bollywood"
    val description: String,
    val posterUrl: String,
    val backdropUrl: String,
    val trailerUrl: String,
    val category: String, // "indian_cinema", "trending", "upcoming", "blockbusters"
    val isDownloaded: Boolean = false,
    val downloadSizeMb: Int = 0,
    val downloadDate: Long = 0L,
    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,
    val isWatched: Boolean = false,
    val userRating: Float = 0f
)
