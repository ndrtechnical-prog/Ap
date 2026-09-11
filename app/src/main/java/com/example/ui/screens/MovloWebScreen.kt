package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.util.Log
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.ai.AiHealedMovieResult
import com.example.ui.BrokenStreamEvent
import com.example.ui.theme.MovloBorder
import com.example.ui.theme.MovloCyan
import com.example.ui.theme.MovloDarkBg
import com.example.ui.theme.MovloDarkCard
import com.example.ui.theme.MovloDarkSurface
import com.example.ui.theme.MovloEmerald
import com.example.ui.theme.MovloGold
import com.example.ui.theme.MovloRed
import com.example.ui.theme.MovloTextMuted
import com.example.ui.theme.MovloTextPrimary
import com.example.ui.theme.MovloTextSecondary
import kotlinx.coroutines.delay

class MovloAiWebBridge(
    private val onBrokenLink: (url: String, title: String?) -> Unit
) {
    @JavascriptInterface
    fun reportBrokenStream(url: String, title: String) {
        onBrokenLink(url, title)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MovloWebScreen(
    isOnline: Boolean,
    isAiHealingEnabled: Boolean,
    isAiThinking: Boolean,
    activeHealedResult: AiHealedMovieResult?,
    onBrokenLinkDetected: (brokenUrl: String, brokenTitle: String?) -> Unit,
    onPlayHealedMovie: () -> Unit,
    onDismissHealedResult: () -> Unit,
    onToggleAiHealing: () -> Unit,
    onOpenOfflineDownloads: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf("https://movlo.site") }
    var pageTitle by remember { mutableStateOf("Movlo Cinema") }
    var isLoading by remember { mutableStateOf(false) }
    var pageProgress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var isMainOfflineError by remember { mutableStateOf(false) }

    // Auto countdown when AI finds an alternative movie
    var autoPlayCountdown by remember { mutableIntStateOf(5) }
    LaunchedEffect(activeHealedResult) {
        if (activeHealedResult != null) {
            autoPlayCountdown = 5
            while (autoPlayCountdown > 0) {
                delay(1000)
                autoPlayCountdown--
            }
            onPlayHealedMovie()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MovloDarkBg)
            .testTag("movlo_web_main_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sleek Top App Bar with Controls, Ad Compatibility, and AI Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MovloDarkCard)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Navigation buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoBack() == true) {
                                webViewInstance?.goBack()
                            }
                        },
                        enabled = canGoBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (canGoBack) MovloTextPrimary else MovloTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoForward() == true) {
                                webViewInstance?.goForward()
                            }
                        },
                        enabled = canGoForward,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (canGoForward) MovloTextPrimary else MovloTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            isMainOfflineError = false
                            webViewInstance?.reload()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = MovloTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // URL & Brand Capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MovloDarkBg)
                        .border(1.dp, MovloBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Web",
                            tint = MovloGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "movlo.site",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MovloTextPrimary
                        )
                    }
                }

                // Right action buttons: AI Healer toggle & Offline Hub
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // AI Guardian Toggle Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isAiHealingEnabled) MovloGold.copy(alpha = 0.2f) else MovloDarkBg
                            )
                            .border(
                                1.dp,
                                if (isAiHealingEnabled) MovloGold else MovloBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onToggleAiHealing() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("ai_healer_toggle_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Healer",
                                tint = if (isAiHealingEnabled) MovloGold else MovloTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAiHealingEnabled) "AI HEALER" else "AI OFF",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAiHealingEnabled) MovloGold else MovloTextMuted
                            )
                        }
                    }

                    // Offline Downloads button
                    IconButton(
                        onClick = onOpenOfflineDownloads,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DownloadDone,
                            contentDescription = "Offline Downloads",
                            tint = MovloEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Progress bar
            if (isLoading && pageProgress < 100) {
                LinearProgressIndicator(
                    progress = { pageProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MovloGold,
                    trackColor = MovloDarkCard
                )
            }

            // Main Edge-to-Edge WebView with Complete Ad Network & Popup Support
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                allowFileAccess = true
                                allowContentAccess = true
                                loadsImagesAutomatically = true

                                // CRITICAL FOR ADS: Support target="_blank", window.open(), and multiple ad windows
                                setSupportMultipleWindows(true)
                                javaScriptCanOpenWindowsAutomatically = true

                                // CRITICAL FOR ADS: Allow mixed HTTP/HTTPS assets used by ad networks
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                                // Allow video ads to autoplay without requiring extra gesture taps
                                mediaPlaybackRequiresUserGesture = false

                                // Offline caching setup
                                cacheMode = if (isOnline) {
                                    WebSettings.LOAD_DEFAULT
                                } else {
                                    WebSettings.LOAD_CACHE_ELSE_NETWORK
                                }
                            }

                            // CRITICAL FOR ADS: Enable first-party and third-party cookies for ad trackers & impressions
                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                            cookieManager.setAcceptThirdPartyCookies(this, true)

                            // Download Listener: handles APK downloads or ad click promotion files
                            setDownloadListener { downloadUrl, _, _, _, _ ->
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                    ctx.startActivity(intent)
                                } catch (e: Exception) {
                                    Log.e("MovloWeb", "Failed to start download intent: $downloadUrl", e)
                                }
                            }

                            // Add AI Javascript Bridge to detect HTML5 video errors & broken embeds
                            addJavascriptInterface(
                                MovloAiWebBridge { brokenUrl, title ->
                                    onBrokenLinkDetected(brokenUrl, title)
                                },
                                "MovloAiBridge"
                            )

                            // WebChromeClient: Handles window.open(), target="_blank" ad popups, and progress
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    pageProgress = newProgress
                                    isLoading = newProgress < 100
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    if (!title.isNullOrBlank()) {
                                        pageTitle = title
                                    }
                                }

                                override fun onGeolocationPermissionsShowPrompt(
                                    origin: String?,
                                    callback: GeolocationPermissions.Callback?
                                ) {
                                    // Grant location permission for interactive geo-targeted ads
                                    callback?.invoke(origin, true, false)
                                }

                                // Handle window.open and target="_blank" from ads properly!
                                override fun onCreateWindow(
                                    view: WebView?,
                                    isDialog: Boolean,
                                    isUserGesture: Boolean,
                                    resultMsg: Message?
                                ): Boolean {
                                    val newWebView = WebView(ctx).apply {
                                        settings.javaScriptEnabled = true
                                        settings.domStorageEnabled = true
                                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                                    }

                                    newWebView.webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(
                                            wView: WebView?,
                                            request: WebResourceRequest?
                                        ): Boolean {
                                            val adUrl = request?.url?.toString() ?: return false
                                            return handleAdAndExternalUrl(ctx, adUrl, this@apply)
                                        }

                                        override fun onRenderProcessGone(
                                            wView: WebView?,
                                            detail: RenderProcessGoneDetail?
                                        ): Boolean {
                                            return true
                                        }
                                    }

                                    val transport = resultMsg?.obj as? WebView.WebViewTransport
                                    transport?.webView = newWebView
                                    resultMsg?.sendToTarget()
                                    return true
                                }
                            }

                            // WebViewClient: Handles ad schemes (market://, intent://, etc.) & broken link detection
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                    url?.let { currentUrl = it }
                                    canGoBack = view?.canGoBack() == true
                                    canGoForward = view?.canGoForward() == true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    canGoBack = view?.canGoBack() == true
                                    canGoForward = view?.canGoForward() == true

                                    // Inject JavaScript to monitor video playback errors and 404 / broken page states
                                    val injectScript = """
                                        (function() {
                                            function checkVideoErrors() {
                                                var videos = document.getElementsByTagName('video');
                                                for (var i = 0; i < videos.length; i++) {
                                                    var v = videos[i];
                                                    v.onerror = function(e) {
                                                        if (window.MovloAiBridge) {
                                                            window.MovloAiBridge.reportBrokenStream(v.currentSrc || window.location.href, document.title);
                                                        }
                                                    };
                                                }
                                                // Check for 404 or stream error text on page
                                                var text = document.body ? document.body.innerText : '';
                                                if (text.includes('404 Not Found') || text.includes('Video not found') || text.includes('Stream expired') || text.includes('This video is unavailable')) {
                                                    if (window.MovloAiBridge) {
                                                        window.MovloAiBridge.reportBrokenStream(window.location.href, document.title);
                                                    }
                                                }
                                            }
                                            checkVideoErrors();
                                            setTimeout(checkVideoErrors, 2500);
                                        })();
                                    """.trimIndent()
                                    view?.evaluateJavascript(injectScript, null)
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    return handleAdAndExternalUrl(ctx, url, view)
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    val reqUrl = request?.url?.toString() ?: ""
                                    if (request?.isForMainFrame == true && !isOnline) {
                                        isMainOfflineError = true
                                    } else if (reqUrl.contains("stream") || reqUrl.contains("movie") || reqUrl.contains("video") || reqUrl.contains("watch")) {
                                        // Broken stream link detected!
                                        onBrokenLinkDetected(reqUrl, view?.title)
                                    }
                                }

                                override fun onReceivedHttpError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    errorResponse: WebResourceResponse?
                                ) {
                                    super.onReceivedHttpError(view, request, errorResponse)
                                    val code = errorResponse?.statusCode ?: 0
                                    val reqUrl = request?.url?.toString() ?: ""
                                    if (code == 404 || code == 410 || code >= 500) {
                                        if (request?.isForMainFrame == true || reqUrl.contains("stream") || reqUrl.contains("movie") || reqUrl.contains("embed")) {
                                            // Broken stream or 404 detected!
                                            onBrokenLinkDetected(reqUrl, view?.title)
                                        }
                                    }
                                }

                                override fun onRenderProcessGone(
                                    view: WebView?,
                                    detail: RenderProcessGoneDetail?
                                ): Boolean {
                                    Log.w("MovloWeb", "Main WebView render process gone: didCrash=${detail?.didCrash()}")
                                    try {
                                        view?.let { wv ->
                                            (wv.parent as? android.view.ViewGroup)?.removeView(wv)
                                            wv.destroy()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("MovloWeb", "Error cleaning up destroyed webview", e)
                                    }
                                    return true
                                }
                            }

                            loadUrl("https://movlo.site")
                            webViewInstance = this
                        }
                    },
                    update = { webView ->
                        webView.settings.cacheMode = if (isOnline) {
                            WebSettings.LOAD_DEFAULT
                        } else {
                            WebSettings.LOAD_CACHE_ELSE_NETWORK
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // If completely offline and main page failed to load: Offline Hub prompt
                if (isMainOfflineError && !isOnline) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MovloDarkBg)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MovloRed.copy(alpha = 0.15f))
                                    .border(1.dp, MovloRed.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = "Offline",
                                    tint = MovloRed,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Text(
                                text = "Offline Mode Active",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MovloTextPrimary
                            )

                            Text(
                                text = "movlo.site live streaming requires internet. Your downloaded offline titles are ready to play with zero buffering!",
                                fontSize = 13.sp,
                                color = MovloTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Button(
                                onClick = onOpenOfflineDownloads,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MovloEmerald,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DownloadDone,
                                    contentDescription = "Downloads",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Watch Offline Movies",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating AI Manual Trigger Button (allows user to trigger AI healing anytime a stream is broken or stuck)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp)
        ) {
            Button(
                onClick = {
                    onBrokenLinkDetected(currentUrl, pageTitle)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MovloGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .border(1.5.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .testTag("ai_manual_heal_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Fix",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI Fix / Next Movie",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // AI Thinking Indicator
        AnimatedVisibility(
            visible = isAiThinking,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MovloDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, MovloGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        color = MovloGold,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Movlo AI Analyzing Stream...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MovloGold
                        )
                        Text(
                            text = "Broken link detected. Gemini AI is selecting a matching alternative movie.",
                            fontSize = 10.sp,
                            color = MovloTextSecondary
                        )
                    }
                }
            }
        }

        // AI Healed Alternative Movie Pop-up / Auto-Player Modal
        AnimatedVisibility(
            visible = activeHealedResult != null,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            activeHealedResult?.let { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, MovloGold, RoundedCornerShape(20.dp))
                        .testTag("ai_healed_movie_card"),
                    colors = CardDefaults.cardColors(containerColor = MovloDarkSurface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(MovloGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Healer",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "AI Broken Stream Recovery",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MovloGold
                                )
                            }

                            IconButton(
                                onClick = onDismissHealedResult,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MovloTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Reason explanation
                        Text(
                            text = result.reason,
                            fontSize = 11.sp,
                            color = MovloTextSecondary,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Movie Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .aspectRatio(2f / 3f)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(result.recommendedMovie.posterUrl)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(id = R.drawable.movlo_action_poster),
                                    error = painterResource(id = R.drawable.movlo_action_poster),
                                    contentDescription = result.recommendedMovie.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = result.recommendedMovie.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MovloTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${result.recommendedMovie.genres} • ${result.recommendedMovie.year}",
                                    fontSize = 11.sp,
                                    color = MovloTextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MovloEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "VERIFIED STREAM READY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MovloEmerald
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons (Auto Play & Dismiss)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onPlayHealedMovie,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MovloGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_play_alternative_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (autoPlayCountdown > 0) "Play Now ($autoPlayCountdown s)" else "Play Now",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onDismissHealedResult,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MovloTextSecondary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Robust handler for Ad clicks, Intent URLs, Play Store referrals, and external links.
 * Returns true if handled externally, false if WebView should load normally.
 */
fun handleAdAndExternalUrl(context: Context, url: String, webView: WebView?): Boolean {
    // 1. Standard HTTP/HTTPS links
    if (url.startsWith("http://") || url.startsWith("https://")) {
        // If it's a Play Store web URL in an ad, launch Play Store app directly for better conversion
        if (url.contains("play.google.com/store/apps/details")) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.setPackage("com.android.vending")
                context.startActivity(intent)
                return true
            } catch (_: Exception) {
                // Let normal browser or webview handle
            }
        }
        return false // Let WebView load the webpage / ad landing page
    }

    // 2. Play Store direct scheme (market://)
    if (url.startsWith("market://")) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            val fallback = "https://play.google.com/store/apps/" + url.substringAfter("market://")
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fallback)))
            } catch (_: Exception) {}
            return true
        }
    }

    // 3. Android Intent scheme (intent://...#Intent;scheme=...;package=...;end)
    // Widely used by ad networks (Google AdSense/AdMob, AppLovin, Unity, Propeller, Monetag, etc.)
    if (url.startsWith("intent://")) {
        try {
            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            if (intent != null) {
                val packageManager = context.packageManager
                val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveInfo != null) {
                    context.startActivity(intent)
                    return true
                } else {
                    // Try fallback URL if target app is not installed
                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                    if (!fallbackUrl.isNullOrEmpty()) {
                        webView?.loadUrl(fallbackUrl)
                        return true
                    }
                    // Try redirecting to Google Play for the package
                    val pack = intent.`package`
                    if (!pack.isNullOrEmpty()) {
                        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pack"))
                        context.startActivity(marketIntent)
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MovloWeb", "Error handling intent scheme: $url", e)
        }
        return true
    }

    // 4. Custom App Schemes (WhatsApp, Telegram, Phone, Mail, SMS)
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
        return true
    } catch (e: Exception) {
        Log.e("MovloWeb", "Unable to open custom scheme: $url", e)
        return true
    }
}
