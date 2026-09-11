package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun MovieCard(
    movie: MovieEntity,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MovloBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("movie_card_${movie.id}"),
        colors = CardDefaults.cardColors(containerColor = MovloDarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Poster Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
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

                // Gradient scrim at bottom of image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MovloDarkCard.copy(alpha = 0.8f)),
                                startY = 120f
                            )
                        )
                )

                // Top Left: Offline Badge or Quality
                if (movie.isDownloaded) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MovloEmerald.copy(alpha = 0.9f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Offline ready",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "OFFLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(0.5.dp, MovloBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = movie.quality,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MovloGold
                        )
                    }
                }

                // Top Right: Rating
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MovloGoldBright,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${movie.rating}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MovloTextPrimary
                        )
                    }
                }

                // Play icon overlay button on poster
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MovloDarkBg.copy(alpha = 0.75f))
                        .border(1.dp, MovloGold.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play ${movie.title}",
                        tint = MovloGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = movie.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MovloTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${movie.genre} • ${movie.year}",
                        fontSize = 10.sp,
                        color = MovloTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Download Action Icon (minimum interactive size >= 48dp handled with padding)
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("download_btn_${movie.id}")
                    ) {
                        Icon(
                            imageVector = if (movie.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                            contentDescription = if (movie.isDownloaded) "Downloaded" else "Download Offline",
                            tint = if (movie.isDownloaded) MovloEmerald else MovloGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
