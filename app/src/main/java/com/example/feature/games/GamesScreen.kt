package com.example.feature.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.ArcadeGameCard
import com.example.domain.models.*
import com.example.ui.theme.*

@Composable
fun GamesScreen(
    gameRecords: Map<String, GameRecord>,
    favoriteIds: Set<String>,
    onGameSelect: (Game) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(GameCategory.ALL) }
    var filterFavoritesOnly by remember { mutableStateOf(false) }

    val allGames = ArcadeCatalog.games

    val filteredGames = remember(allGames, searchQuery, selectedCategory, filterFavoritesOnly, favoriteIds) {
        allGames.filter { game ->
            val matchesQuery = game.title.contains(searchQuery, ignoreCase = true) ||
                    game.description.contains(searchQuery, ignoreCase = true) ||
                    game.category.displayName.contains(searchQuery, ignoreCase = true)

            val matchesCategory = (selectedCategory == GameCategory.ALL) || (game.category == selectedCategory)
            val matchesFav = !filterFavoritesOnly || favoriteIds.contains(game.id)

            matchesQuery && matchesCategory && matchesFav
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("games_screen_list"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search & Filter Header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("games_search_input"),
                    placeholder = { Text("Search 20+ mini games...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Favorites Toggle Pill
                    FilterChip(
                        selected = filterFavoritesOnly,
                        onClick = { filterFavoritesOnly = !filterFavoritesOnly },
                        label = { Text("Favorites ❤️") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ArcadePink.copy(alpha = 0.2f),
                            selectedLabelColor = ArcadePink
                        )
                    )

                    GameCategory.values().forEach { cat ->
                        FilterChip(
                            selected = (selectedCategory == cat && !filterFavoritesOnly),
                            onClick = {
                                filterFavoritesOnly = false
                                selectedCategory = cat
                            },
                            label = { Text("${cat.iconEmoji} ${cat.displayName}") }
                        )
                    }
                }
            }
        }

        // Section header with count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (filterFavoritesOnly) "Favorite Games" else "${selectedCategory.displayName} Games",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "${filteredGames.size} Games",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (filteredGames.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎮", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (filterFavoritesOnly) "No Favorites Yet" else "No Games Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (filterFavoritesOnly) "Tap the heart on any game card to add it here!" else "Try a different search or filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredGames) { game ->
                val record = gameRecords[game.id]
                val isFav = favoriteIds.contains(game.id)
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ArcadeGameCard(
                        game = game,
                        highScore = record?.highScore ?: 0,
                        isFavorite = isFav,
                        onFavoriteToggle = { onFavoriteToggle(game.id, isFav) },
                        onClick = { onGameSelect(game) }
                    )
                }
            }
        }
    }
}
