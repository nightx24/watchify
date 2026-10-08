package com.nightx24.watchify

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

data class MediaSite(
    val name: String,
    val url: String,
    val category: String,
    val description: String
)

private val sites = listOf(
    MediaSite("Netflix", "https://www.netflix.com", "Movies", "Movies, series and originals"),
    MediaSite("Prime Video", "https://www.primevideo.com", "Movies", "Movies, series and originals"),
    MediaSite("Disney+", "https://www.disneyplus.com", "Movies", "Disney, Pixar, Marvel and more"),
    MediaSite("YouTube", "https://www.youtube.com", "Video", "Official videos and creators"),
    MediaSite("Crunchyroll", "https://www.crunchyroll.com", "Anime", "Anime and Asian entertainment"),
    MediaSite("Tubi", "https://tubitv.com", "Free", "Free ad-supported streaming"),
    MediaSite("Pluto TV", "https://pluto.tv", "Free", "Free live channels and on-demand"),
    MediaSite("Internet Archive", "https://archive.org", "Archive", "Public-domain and archived media")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WatchifyApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchifyApp() {
    var selectedSite by remember { mutableStateOf<MediaSite?>(null) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }
    val favorites = remember { mutableStateOf(setOf<String>()) }

    if (selectedSite != null) {
        BrowserScreen(site = selectedSite!!, onBack = { selectedSite = null })
        return
    }

    val categories = listOf("All", "Movies", "Anime", "Video", "Free", "Archive")
    val filtered = sites.filter {
        (category == "All" || it.category == category) &&
            (query.isBlank() || it.name.contains(query, true) || it.description.contains(query, true))
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Watchify")
                            Text("Your entertainment hub", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Text("⌕", style = MaterialTheme.typography.titleLarge) },
                    placeholder = { Text("Search sites") },
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { category = item },
                            label = { Text(item) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered) { site ->
                        SiteCard(
                            site = site,
                            favorite = favorites.value.contains(site.name),
                            onFavorite = {
                                favorites.value = if (favorites.value.contains(site.name)) {
                                    favorites.value - site.name
                                } else {
                                    favorites.value + site.name
                                }
                            },
                            onOpen = { selectedSite = site }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SiteCard(
    site: MediaSite,
    favorite: Boolean,
    onFavorite: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(site.name.take(1), style = MaterialTheme.typography.titleLarge)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(site.name, style = MaterialTheme.typography.titleMedium)
                    Text(site.category, style = MaterialTheme.typography.labelMedium)
                }
                IconButton(onClick = onFavorite) {
                    Text(
                        if (favorite) "♥" else "♡",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(site.description, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("★", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.width(4.dp))
                Text("Open in Watchify", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrowserScreen(site: MediaSite, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(site.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("‹", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { context: Context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.safeBrowsingEnabled = true
                    loadUrl(site.url)
                }
            }
        )
    }
}
