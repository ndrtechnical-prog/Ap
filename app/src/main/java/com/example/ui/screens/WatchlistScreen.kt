package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.local.MovieEntity
import com.example.ui.theme.MovloBorder
import com.example.ui.theme.MovloDarkBg
import com.example.ui.theme.MovloDarkCard
import com.example.ui.theme.MovloEmerald
import com.example.ui.theme.MovloGold
import com.example.ui.theme.MovloGoldBright
import com.example.ui.theme.MovloTextMuted
import com.example.ui.theme.MovloTextPrimary
import com.example.ui.theme.MovloTextSecondary

@Composable
fun WatchlistScreen(
    watchlistMovies: List<MovieEntity>,
    onMovieClick: (MovieEntity) -> Unit,
    onDownloadClick: (MovieEntity) -> Unit,
    onRemoveFromWatchlist: (MovieEntity) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MovloDarkBg)
            .testTag("watchlist_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp)
    ) {
        item {
            Text(
                text = "My Watchlist",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MovloTextPrimary
            )
            Text(
                text = "Saved movies & trailers ready for instant viewing",
                fontSize = 12.sp,
                color = MovloTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (watchlistMovies.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MovloDarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Empty watchlist",
                                tint = MovloTextMuted,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = "Your Watchlist is Empty",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MovloTextPrimary
                        )
                        Text(
                            text = "Bookmark upcoming releases, trailers, and Indian cinema blockbusters to view later.",
                            fontSize = 12.sp,
                            color = MovloTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onExploreClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MovloGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Find Movies", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(watchlistMovies) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, MovloBorder, RoundedCornerShape(14.dp))
                        .clickable { onMovieClick(movie) }
                        .testTag("watchlist_item_${movie.id}"),
                    colors = CardDefaults.cardColors(containerColor = MovloDarkCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Thumbnail
                        Box(
                            modifier = Modifier
                                .width(74.dp)
                                .aspectRatio(2f / 3f)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(movie.posterUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(id = R.drawable.movlo_action_poster),
                                error = painterResource(id = R.drawable.movlo_action_poster),
                                contentDescription = movie.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = movie.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MovloTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = MovloGoldBright,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${movie.rating}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MovloTextPrimary
                                )
                                Text(
                                    text = "• ${movie.duration} • ${movie.year}",
                                    fontSize = 11.sp,
                                    color = MovloTextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = movie.genres,
                                fontSize = 11.sp,
                                color = MovloTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Actions (Play, Download, Remove)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = { onDownloadClick(movie) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (movie.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                                    contentDescription = "Download",
                                    tint = if (movie.isDownloaded) MovloEmerald else MovloGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { onRemoveFromWatchlist(movie) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkRemove,
                                    contentDescription = "Remove from watchlist",
                                    tint = MovloTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
