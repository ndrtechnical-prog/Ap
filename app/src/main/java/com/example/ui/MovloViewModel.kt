package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiHealedMovieResult
import com.example.data.ai.GeminiMovieHealer
import com.example.data.local.MovieEntity
import com.example.data.local.MovloDatabase
import com.example.data.repository.MovieRepository
import com.example.util.NetworkConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BrokenStreamEvent(
    val url: String,
    val title: String?,
    val timestamp: Long = System.currentTimeMillis()
)

class MovloViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MovloDatabase.getDatabase(application)
    private val repository = MovieRepository(database.movieDao())
    private val networkObserver = NetworkConnectivityObserver(application)

    val isOnline: StateFlow<Boolean> = networkObserver.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("all")
    val selectedMovieForPlayer = MutableStateFlow<MovieEntity?>(null)
    val userNotice = MutableStateFlow<String?>(null)

    // AI Stream Healing states
    val isAiHealingEnabled = MutableStateFlow(true)
    val isAiThinking = MutableStateFlow(false)
    val currentBrokenEvent = MutableStateFlow<BrokenStreamEvent?>(null)
    val activeHealedResult = MutableStateFlow<AiHealedMovieResult?>(null)

    val allMovies: StateFlow<List<MovieEntity>> = repository.allMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedMovies: StateFlow<List<MovieEntity>> = repository.downloadedMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchlistMovies: StateFlow<List<MovieEntity>> = repository.watchlistMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMovies: StateFlow<List<MovieEntity>> = combine(
        allMovies,
        searchQuery,
        selectedCategory
    ) { movies, query, category ->
        var list = movies

        if (category != "all") {
            list = when (category) {
                "indian_cinema" -> list.filter { it.category == "indian_cinema" || it.genres.contains("Indian", ignoreCase = true) || it.genres.contains("Bollywood", ignoreCase = true) }
                "trending" -> list.filter { it.category == "trending" || it.rating >= 8.8 }
                "upcoming" -> list.filter { it.category == "upcoming" || it.year >= 2026 }
                "action" -> list.filter { it.genres.contains("Action", ignoreCase = true) }
                "comedy" -> list.filter { it.genres.contains("Comedy", ignoreCase = true) }
                "scifi" -> list.filter { it.genres.contains("Sci-Fi", ignoreCase = true) }
                else -> list
            }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.genre.lowercase().contains(q) ||
                it.genres.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    fun setQuery(query: String) {
        searchQuery.value = query
    }

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    fun toggleDownload(movie: MovieEntity) {
        viewModelScope.launch {
            repository.toggleDownload(movie)
            val action = if (!movie.isDownloaded) "Downloaded for Offline Access: ${movie.title}" else "Removed from Offline Storage: ${movie.title}"
            userNotice.value = action
        }
    }

    fun toggleWatchlist(movie: MovieEntity) {
        viewModelScope.launch {
            repository.toggleWatchlist(movie)
            userNotice.value = if (!movie.isInWatchlist) "Added to Watchlist" else "Removed from Watchlist"
        }
    }

    fun toggleFavorite(movie: MovieEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(movie)
        }
    }

    fun clearAllDownloads() {
        viewModelScope.launch {
            repository.clearAllDownloads()
            userNotice.value = "Cleared all offline downloads"
        }
    }

    fun openPlayer(movie: MovieEntity) {
        selectedMovieForPlayer.value = movie
    }

    fun closePlayer() {
        selectedMovieForPlayer.value = null
    }

    fun clearNotice() {
        userNotice.value = null
    }

    fun toggleAiHealing() {
        isAiHealingEnabled.value = !isAiHealingEnabled.value
        userNotice.value = if (isAiHealingEnabled.value) "AI Broken Stream Recovery: ENABLED" else "AI Broken Stream Recovery: DISABLED"
    }

    fun onBrokenLinkDetected(brokenUrl: String, brokenTitle: String?) {
        if (!isAiHealingEnabled.value) return

        viewModelScope.launch {
            isAiThinking.value = true
            currentBrokenEvent.value = BrokenStreamEvent(brokenUrl, brokenTitle)

            val catalog = allMovies.value.ifEmpty {
                // Ensure at least repository has items
                repository.prepopulateIfEmpty()
                allMovies.value
            }

            val healer = GeminiMovieHealer(catalog)
            val result = healer.resolveBrokenLink(brokenUrl, brokenTitle)

            isAiThinking.value = false
            activeHealedResult.value = result

            // Show notice
            userNotice.value = "🤖 AI Healer: Broken stream detected! Auto-playing '${result.recommendedMovie.title}'"
        }
    }

    fun playHealedMovie() {
        activeHealedResult.value?.let { result ->
            selectedMovieForPlayer.value = result.recommendedMovie
            activeHealedResult.value = null
        }
    }

    fun dismissHealedResult() {
        activeHealedResult.value = null
        currentBrokenEvent.value = null
    }
}
