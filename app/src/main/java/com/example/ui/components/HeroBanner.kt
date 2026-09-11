package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.MovieEntity
import com.example.ui.theme.MovloBorder
import com.example.ui.theme.MovloDarkBg
import com.example.ui.theme.MovloDarkCard
import com.example.ui.theme.MovloEmerald
import com.example.ui.theme.MovloGold
import com.example.ui.theme.MovloGoldBright
import com.example.ui.theme.MovloRed
import com.example.ui.theme.MovloTextPrimary
import com.example.ui.theme.MovloTextSecondary

@Composable
fun HeroBanner(
    movie: MovieEntity,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, MovloBorder, RoundedCornerShape(18.dp))
            .testTag("hero_banner_${movie.id}")
    ) {
        // Hero Background Image with fallback to generated high-res backdrop
        Image(
            painter = painterResource(id = R.drawable.movlo_hero_banner),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Dark Overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MovloDarkBg.copy(alpha = 0.5f),
                            MovloDarkBg.copy(alpha = 0.95f)
                        ),
                        startY = 100f
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Badges row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MovloRed)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "FEATURED CINEMA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                // Quality
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MovloDarkCard.copy(alpha = 0.85f))
                        .border(1.dp, MovloBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = movie.quality,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MovloGold
                    )
                }

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MovloDarkCard.copy(alpha = 0.85f))
                        .border(1.dp, MovloBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MovloGoldBright,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${movie.rating}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MovloTextPrimary
                    )
                }

                // Year & Duration
                Text(
                    text = "${movie.year} • ${movie.duration}",
                    fontSize = 11.sp,
                    color = MovloTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = movie.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MovloTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = movie.description,
                fontSize = 12.sp,
                color = MovloTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play Button
                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MovloGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("hero_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Play Clip",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Offline Download Button
                OutlinedButton(
                    onClick = onDownloadClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (movie.isDownloaded) MovloEmerald.copy(alpha = 0.15f) else MovloDarkCard,
                        contentColor = if (movie.isDownloaded) MovloEmerald else MovloTextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (movie.isDownloaded) MovloEmerald else MovloBorder
                    ),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                        .testTag("hero_download_button")
                ) {
                    Icon(
                        imageVector = if (movie.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                        contentDescription = "Offline Access",
                        tint = if (movie.isDownloaded) MovloEmerald else MovloGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (movie.isDownloaded) "Offline Ready" else "Save Offline",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                // Watchlist Button
                OutlinedButton(
                    onClick = onWatchlistClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MovloDarkCard,
                        contentColor = if (movie.isInWatchlist) MovloGold else MovloTextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MovloBorder),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("hero_watchlist_button")
                ) {
                    Icon(
                        imageVector = if (movie.isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Watchlist",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
