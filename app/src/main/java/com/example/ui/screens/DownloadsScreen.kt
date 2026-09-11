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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.MovloCyan
import com.example.ui.theme.MovloDarkBg
import com.example.ui.theme.MovloDarkCard
import com.example.ui.theme.MovloDarkSurface
import com.example.ui.theme.MovloEmerald
import com.example.ui.theme.MovloGold
import com.example.ui.theme.MovloGoldBright
import com.example.ui.theme.MovloRed
import com.example.ui.theme.MovloTextMuted
import com.example.ui.theme.MovloTextPrimary
import com.example.ui.theme.MovloTextSecondary

@Composable
fun DownloadsScreen(
    downloadedMovies: List<MovieEntity>,
    onMovieClick: (MovieEntity) -> Unit,
    onDeleteDownload: (MovieEntity) -> Unit,
    onClearAll: () -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalMb = downloadedMovies.sumOf { it.downloadSizeMb }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MovloDarkBg)
            .testTag("downloads_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Offline Downloads",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MovloTextPrimary
                    )
                    Text(
                        text = "Zero-buffering playback anytime without internet",
                        fontSize = 12.sp,
                        color = MovloTextSecondary
                    )
                }

                if (downloadedMovies.isNotEmpty()) {
                    IconButton(
                        onClick = onClearAll,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MovloDarkCard)
                            .testTag("clear_all_downloads_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear all downloads",
                            tint = MovloRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Storage Management Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MovloBorder, RoundedCornerShape(16.dp))
                    .testTag("storage_info_card"),
                colors = CardDefaults.cardColors(containerColor = MovloDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MovloEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SdStorage,
                                    contentDescription = "Storage",
                                    tint = MovloEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Device Offline Storage",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MovloTextPrimary
                                )
                                Text(
                                    text = "${downloadedMovies.size} Movies saved • ${totalMb} MB used",
                                    fontSize = 11.sp,
                                    color = MovloEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MovloEmerald)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "READY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (totalMb / 3000f).coerceIn(0.05f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MovloEmerald,
                        trackColor = MovloDarkCard
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Movlo Offline Cache: ${totalMb} MB",
                            fontSize = 10.sp,
                            color = MovloTextMuted
                        )
                        Text(
                            text = "High Quality 1080p MP4",
                            fontSize = 10.sp,
                            color = MovloTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Content
        if (downloadedMovies.isEmpty()) {
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
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = "No downloads",
                                tint = MovloTextMuted,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = "No Offline Downloads Yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MovloTextPrimary
                        )
                        Text(
                            text = "Download movies and trailers from Movlo to watch seamlessly when you are on flights, trains, or without internet.",
                            fontSize = 12.sp,
                            color = MovloTextSecondary,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            lineHeight = 17.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onExploreClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MovloGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("explore_catalog_btn")
                        ) {
                            Text(
                                text = "Browse Movlo Catalog",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            items(downloadedMovies) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, MovloBorder, RoundedCornerShape(14.dp))
                        .clickable { onMovieClick(movie) }
                        .testTag("downloaded_item_${movie.id}"),
                    colors = CardDefaults.cardColors(containerColor = MovloDarkCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Poster thumbnail
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

                            // Overlay Play Button
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = MovloGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Movie Info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = movie.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MovloTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

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

                            Spacer(modifier = Modifier.height(4.dp))

                            // Offline stats tag
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MovloEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${movie.downloadSizeMb} MB",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MovloEmerald
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MovloDarkSurface)
                                        .border(0.5.dp, MovloBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = movie.quality,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MovloGold
                                    )
                                }
                            }
                        }

                        // Right actions (Play button & delete)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { onMovieClick(movie) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MovloGold)
                                    .testTag("play_offline_${movie.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play offline",
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { onDeleteDownload(movie) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("delete_download_${movie.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete download",
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
