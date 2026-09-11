package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.MovloWebScreen
import com.example.ui.theme.MovloBorder
import com.example.ui.theme.MovloDarkBg
import com.example.ui.theme.MovloDarkCard
import com.example.ui.theme.MovloEmerald
import com.example.ui.theme.MovloGold
import com.example.ui.theme.MovloTextPrimary
import com.example.ui.theme.MovloTextSecondary

enum class ActiveScreen {
    WEBSITE,
    OFFLINE_DOWNLOADS
}

@Composable
fun MovloMainScreen(viewModel: MovloViewModel) {
    // By default, open directly to Movlo.site as requested ("sirf Website open How Movlo.site")
    var currentScreen by remember { mutableStateOf(ActiveScreen.WEBSITE) }

    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val downloadedMovies by viewModel.downloadedMovies.collectAsStateWithLifecycle()
    val selectedMovieForPlayer by viewModel.selectedMovieForPlayer.collectAsStateWithLifecycle()
    val userNotice by viewModel.userNotice.collectAsStateWithLifecycle()

    // AI states
    val isAiHealingEnabled by viewModel.isAiHealingEnabled.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val activeHealedResult by viewModel.activeHealedResult.collectAsStateWithLifecycle()

    // Auto dismiss user notice after 3.5s
    LaunchedEffect(userNotice) {
        if (userNotice != null) {
            kotlinx.coroutines.delay(3500)
            viewModel.clearNotice()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        containerColor = MovloDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ActiveScreen.WEBSITE -> {
                    MovloWebScreen(
                        isOnline = isOnline,
                        isAiHealingEnabled = isAiHealingEnabled,
                        isAiThinking = isAiThinking,
                        activeHealedResult = activeHealedResult,
                        onBrokenLinkDetected = { url, title ->
                            viewModel.onBrokenLinkDetected(url, title)
                        },
                        onPlayHealedMovie = {
                            viewModel.playHealedMovie()
                        },
                        onDismissHealedResult = {
                            viewModel.dismissHealedResult()
                        },
                        onToggleAiHealing = {
                            viewModel.toggleAiHealing()
                        },
                        onOpenOfflineDownloads = {
                            currentScreen = ActiveScreen.OFFLINE_DOWNLOADS
                        }
                    )
                }
                ActiveScreen.OFFLINE_DOWNLOADS -> {
                    DownloadsScreen(
                        downloadedMovies = downloadedMovies,
                        onMovieClick = { viewModel.openPlayer(it) },
                        onDeleteDownload = { viewModel.toggleDownload(it) },
                        onClearAll = { viewModel.clearAllDownloads() },
                        onExploreClick = { currentScreen = ActiveScreen.WEBSITE }
                    )
                }
            }

            // Floating feedback notice toast
            AnimatedVisibility(
                visible = userNotice != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 76.dp, start = 16.dp, end = 16.dp)
            ) {
                userNotice?.let { notice ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MovloDarkCard)
                            .border(1.dp, MovloGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (notice.contains("AI")) Icons.Default.AutoAwesome else Icons.Default.Info,
                                contentDescription = "Notice",
                                tint = MovloGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = notice,
                                fontSize = 12.sp,
                                color = MovloTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Built-in Player Dialog when a movie is played (from AI healing or offline downloads)
            selectedMovieForPlayer?.let { movie ->
                VideoPlayerDialog(
                    movie = movie,
                    isOnline = isOnline,
                    onDismiss = { viewModel.closePlayer() },
                    onToggleDownload = { viewModel.toggleDownload(movie) },
                    onToggleWatchlist = { viewModel.toggleWatchlist(movie) }
                )
            }
        }
    }
}
