package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerDialog(
    movie: MovieEntity,
    isOnline: Boolean,
    onDismiss: () -> Unit,
    onToggleDownload: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableFloatStateOf(0.18f) }
    var isMuted by remember { mutableStateOf(false) }

    // Playback progress ticker
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            if (currentProgress < 1f) {
                currentProgress += 0.005f
            } else {
                currentProgress = 0f
                isPlaying = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MovloDarkBg)
                .testTag("video_player_modal"),
            color = MovloDarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Video Screen Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                ) {
                    // Video Backdrop / Frame
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(movie.backdropUrl.ifEmpty { movie.posterUrl })
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.movlo_hero_banner),
                        error = painterResource(id = R.drawable.movlo_hero_banner),
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    // Top Bar inside Player (Back/Close, Quality Chip, Mute)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .testTag("player_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Player",
                                tint = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MovloDarkCard.copy(alpha = 0.8f))
                                    .border(1.dp, MovloGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = movie.quality,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MovloGold
                                )
                            }

                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeDown else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Audio toggle",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Center Play / Rewind / Fast-Forward Controls
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                currentProgress = (currentProgress - 0.05f).coerceAtLeast(0f)
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Rewind 10s",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MovloGold)
                                .testTag("player_toggle_play")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                currentProgress = (currentProgress + 0.05f).coerceAtMost(1f)
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Forward 10s",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Bottom Player Scrubber Bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val totalSeconds = 150 // trailer duration ~2:30
                            val currentSec = (currentProgress * totalSeconds).toInt()
                            val curM = currentSec / 60
                            val curS = currentSec % 60
                            val totM = totalSeconds / 60
                            val totS = totalSeconds % 60

                            Text(
                                text = String.format("%02d:%02d", curM, curS),
                                fontSize = 11.sp,
                                color = MovloTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = String.format("%02d:%02d", totM, totS),
                                fontSize = 11.sp,
                                color = MovloTextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Slider(
                            value = currentProgress,
                            onValueChange = { currentProgress = it },
                            colors = SliderDefaults.colors(
                                thumbColor = MovloGold,
                                activeTrackColor = MovloGold,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Offline Playback State Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (movie.isDownloaded) MovloEmerald.copy(alpha = 0.15f)
                            else if (!isOnline) MovloRed.copy(alpha = 0.15f)
                            else MovloDarkCard
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (movie.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                            contentDescription = "Status",
                            tint = if (movie.isDownloaded) MovloEmerald else MovloGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (movie.isDownloaded)
                                "Offline Local Clip Active — Playing directly from device memory (No internet used)"
                            else if (!isOnline)
                                "Offline Mode — Download available clips to enjoy seamless buffer-free playback"
                            else
                                "Online Streaming Active — Save this title to access it anytime without internet",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (movie.isDownloaded) MovloEmerald else MovloTextPrimary
                        )
                    }
                }

                // Movie Information Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = movie.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MovloTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = MovloGoldBright,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${movie.rating}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MovloTextPrimary
                                    )
                                }
                                Text(
                                    text = "•",
                                    color = MovloTextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${movie.year}",
                                    fontSize = 12.sp,
                                    color = MovloTextSecondary
                                )
                                Text(
                                    text = "•",
                                    color = MovloTextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = movie.duration,
                                    fontSize = 12.sp,
                                    color = MovloTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons (Offline Download / Watchlist)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onToggleDownload,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (movie.isDownloaded) MovloEmerald else MovloGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("modal_download_button")
                        ) {
                            Icon(
                                imageVector = if (movie.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                                contentDescription = "Offline Action",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (movie.isDownloaded) "Downloaded (${movie.downloadSizeMb} MB)" else "Save for Offline",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onToggleWatchlist,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MovloDarkCard,
                                contentColor = if (movie.isInWatchlist) MovloGold else MovloTextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MovloBorder),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("modal_watchlist_button")
                        ) {
                            Icon(
                                imageVector = if (movie.isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Watchlist",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (movie.isInWatchlist) "In Watchlist" else "Watchlist",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Genres tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        movie.genres.split(",").forEach { genreTag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MovloDarkCard)
                                    .border(1.dp, MovloBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = genreTag.trim(),
                                    fontSize = 11.sp,
                                    color = MovloTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Synopsis
                    Text(
                        text = "SYNOPSIS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MovloGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = movie.description,
                        fontSize = 13.sp,
                        color = MovloTextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Offline storage card info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MovloDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MovloBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Offline Access Specifications",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MovloTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Offline Cache Status: ${if (movie.isDownloaded) "Cached Locally on Device" else "Ready to download (approx 512 MB)"}",
                                fontSize = 11.sp,
                                color = MovloTextMuted
                            )
                            Text(
                                text = "• Source: Movlo.site Cinema Collection",
                                fontSize = 11.sp,
                                color = MovloTextMuted
                            )
                            Text(
                                text = "• Resolution: 1080p Full HD Audio/Video Stream",
                                fontSize = 11.sp,
                                color = MovloTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
