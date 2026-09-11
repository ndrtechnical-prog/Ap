package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.ui.theme.MovloBorder
import com.example.ui.theme.MovloCyan
import com.example.ui.theme.MovloDarkBg
import com.example.ui.theme.MovloDarkCard
import com.example.ui.theme.MovloEmerald
import com.example.ui.theme.MovloGold
import com.example.ui.theme.MovloGoldBright
import com.example.ui.theme.MovloRed
import com.example.ui.theme.MovloTextMuted
import com.example.ui.theme.MovloTextPrimary
import com.example.ui.theme.MovloTextSecondary

@Composable
fun MovloTopBar(
    isOnline: Boolean,
    downloadCount: Int,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MovloDarkBg.copy(alpha = 0.95f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("brand_header")
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(MovloGold, MovloGoldBright)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Movlo Logo",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MOVLO",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                color = MovloTextPrimary
                            )
                            Text(
                                text = ".SITE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MovloGold,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                        Text(
                            text = "Cinema & Offline Stream",
                            fontSize = 10.sp,
                            color = MovloTextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Right controls: Online/Offline Status Pill & Search
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isOnline) MovloDarkCard else MovloRed.copy(alpha = 0.2f))
                            .border(
                                1.dp,
                                if (isOnline) MovloEmerald.copy(alpha = 0.6f) else MovloRed,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("network_status_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) MovloEmerald else MovloRed)
                            )
                            Text(
                                text = if (isOnline) "LIVE" else "OFFLINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnline) MovloEmerald else MovloRed
                            )
                        }
                    }

                    // Search Button
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MovloDarkCard)
                            .testTag("top_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search movies",
                            tint = MovloTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
