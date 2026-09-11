package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MovieEntity
import com.example.ui.components.HeroBanner
import com.example.ui.components.MovieCard
import com.example.ui.theme.MovloBorder
import com.example.ui.theme.MovloCyan
import com.example.ui.theme.MovloDarkBg
import com.example.ui.theme.MovloDarkCard
import com.example.ui.theme.MovloEmerald
import com.example.ui.theme.MovloGold
import com.example.ui.theme.MovloRed
import com.example.ui.theme.MovloTextMuted
import com.example.ui.theme.MovloTextPrimary
import com.example.ui.theme.MovloTextSecondary

@Composable
fun HomeScreen(
    movies: List<MovieEntity>,
    allMovies: List<MovieEntity>,
    searchQuery: String,
    selectedCategory: String,
    isOnline: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onMovieClick: (MovieEntity) -> Unit,
    onDownloadClick: (MovieEntity) -> Unit,
    onWatchlistClick: (MovieEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "all" to "All Titles",
        "indian_cinema" to "Indian Cinema",
        "trending" to "Trending Now",
        "upcoming" to "Upcoming Trailers",
        "action" to "Action",
        "comedy" to "Comedy",
        "scifi" to "Sci-Fi"
    )

    // Featured hero item (prefer Dune 3 or first blockbuster)
    val featuredMovie = allMovies.firstOrNull { it.id == "movlo_dune_3" } ?: allMovies.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MovloDarkBg)
            .testTag("home_screen_feed"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Offline Notice Banner
        if (!isOnline) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MovloRed.copy(alpha = 0.18f))
                        .border(1.dp, MovloRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .testTag("offline_alert_banner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "Offline Mode",
                            tint = MovloRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Offline Mode Active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Movlo cache enabled: You have full access to cached movies & offline downloads without internet.",
                                fontSize = 11.sp,
                                color = MovloTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = "Search movies, genres, Bollywood, trailers...",
                            fontSize = 13.sp,
                            color = MovloTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MovloGold,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MovloTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MovloDarkCard,
                        unfocusedContainerColor = MovloDarkCard,
                        focusedBorderColor = MovloGold,
                        unfocusedBorderColor = MovloBorder,
                        focusedTextColor = MovloTextPrimary,
                        unfocusedTextColor = MovloTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field")
                )
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                categories.forEach { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) MovloGold else MovloDarkCard
                            )
                            .border(
                                1.dp,
                                if (isSelected) MovloGold else MovloBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onCategoryChange(catKey) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("filter_chip_$catKey")
                    ) {
                        Text(
                            text = catLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MovloTextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // If in "All" view with no search query: Show Hero Banner
        if (selectedCategory == "all" && searchQuery.isBlank() && featuredMovie != null) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    HeroBanner(
                        movie = featuredMovie,
                        onPlayClick = { onMovieClick(featuredMovie) },
                        onDownloadClick = { onDownloadClick(featuredMovie) },
                        onWatchlistClick = { onWatchlistClick(featuredMovie) }
                    )
                }
            }

            // Section: Indian Cinema Blockbusters (Direct from movlo.site)
            val indianCinemaMovies = allMovies.filter {
                it.category == "indian_cinema" || it.genres.contains("Indian", ignoreCase = true)
            }
            if (indianCinemaMovies.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp, 16.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(MovloGold)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Indian Cinema Classics",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MovloTextPrimary
                                )
                            }
                            Text(
                                text = "See All",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MovloGold,
                                modifier = Modifier.clickable { onCategoryChange("indian_cinema") }
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(indianCinemaMovies) { movie ->
                                MovieCard(
                                    movie = movie,
                                    onClick = { onMovieClick(movie) },
                                    onDownloadClick = { onDownloadClick(movie) }
                                )
                            }
                        }
                    }
                }
            }

            // Section: Upcoming Blockbuster Trailers (2026/2027)
            val upcomingMovies = allMovies.filter { it.category == "upcoming" || it.year >= 2026 }
            if (upcomingMovies.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp, 16.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(MovloCyan)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Upcoming Trailers (2026–2027)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MovloTextPrimary
                                )
                            }
                            Text(
                                text = "See All",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MovloGold,
                                modifier = Modifier.clickable { onCategoryChange("upcoming") }
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(upcomingMovies) { movie ->
                                MovieCard(
                                    movie = movie,
                                    onClick = { onMovieClick(movie) },
                                    onDownloadClick = { onDownloadClick(movie) }
                                )
                            }
                        }
                    }
                }
            }

            // Section: All Catalog
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp, 16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MovloEmerald)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Explore All Movlo Movies & Clips",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MovloTextPrimary
                    )
                }
            }
        }

        // Filtered Grid / Rows
        val displayList = movies
        if (displayList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "No movies",
                            tint = MovloTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No movies match your search",
                            fontSize = 14.sp,
                            color = MovloTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            // Group in pairs of 2 for clean 2-column grid layout
            val chunkedMovies = displayList.chunked(2)
            items(chunkedMovies) { rowMovies ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (movie in rowMovies) {
                        Box(modifier = Modifier.weight(1f)) {
                            MovieCard(
                                movie = movie,
                                onClick = { onMovieClick(movie) },
                                onDownloadClick = { onDownloadClick(movie) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    if (rowMovies.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
