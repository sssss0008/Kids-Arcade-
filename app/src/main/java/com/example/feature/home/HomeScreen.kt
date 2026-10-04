package com.example.feature.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.ArcadeGameCard
import com.example.core.ui.PlayerProgressCard
import com.example.domain.models.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    profile: PlayerProfile,
    gameRecords: Map<String, GameRecord>,
    favoriteIds: Set<String>,
    onGameSelect: (Game) -> Unit,
    onRandomGameSelect: () -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allGames = ArcadeCatalog.games
    val popularGames = remember(allGames) { allGames.filter { it.isPopular } }

    // Last played game for Continue Playing
    val lastPlayedRecord = gameRecords.values.maxByOrNull { it.lastPlayedTimestamp }
    val lastPlayedGame = allGames.find { it.id == lastPlayedRecord?.gameId } ?: allGames.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Arcade Section
        item {
            ArcadeHeroSection(
                profile = profile,
                onPlayClick = { onGameSelect(lastPlayedGame) }
            )
        }

        // Player Progress Summary
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                PlayerProgressCard(
                    profile = profile,
                    onClick = onProfileClick
                )
            }
        }

        // Continue Playing Section
        item {
            ContinuePlayingSection(
                game = lastPlayedGame,
                record = lastPlayedRecord,
                onPlayClick = { onGameSelect(lastPlayedGame) }
            )
        }

        // Daily Challenge Card
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                DailyChallengeCard(
                    challenge = ArcadeCatalog.dailyChallenge,
                    onPlayChallengeGame = {
                        val g = allGames.find { it.id == ArcadeCatalog.dailyChallenge.targetGameId } ?: allGames.first()
                        onGameSelect(g)
                    }
                )
            }
        }

        // Quick Play Row
        item {
            QuickPlaySection(
                onRandomGame = onRandomGameSelect,
                onReflexTest = {
                    val g = allGames.find { it.id == "reflex_master" } ?: allGames.first()
                    onGameSelect(g)
                },
                onSpeedChallenge = {
                    val g = allGames.find { it.id == "fast_fingers" } ?: allGames.first()
                    onGameSelect(g)
                },
                onMemoryTest = {
                    val g = allGames.find { it.id == "memory_cards" } ?: allGames.first()
                    onGameSelect(g)
                }
            )
        }

        // Popular Arcade Games
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Games 🔥",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${popularGames.size} Games",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(popularGames) { game ->
            val record = gameRecords[game.id]
            val isFav = favoriteIds.contains(game.id)
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
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

@Composable
fun ArcadeHeroSection(
    profile: PlayerProfile,
    onPlayClick: () -> Unit
) {
    // Subtle float animation
    val infiniteTransition = rememberInfiniteTransition(label = "hero_particles")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_float"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ArcadePurpleDeep,
                            ArcadeIndigoPrimary
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Floating Avatar & Arcade Mascot
                Box(
                    modifier = Modifier
                        .offset(y = floatOffset.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(profile.avatarEmoji, fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Welcome to Kids Arcade Mania!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Play, explore, challenge yourself, and become the Ultimate Champion! 👑",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.88f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Play Button
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .testTag("hero_play_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArcadeGold)
                ) {
                    Text("🕹️ START PLAYING NOW", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ContinuePlayingSection(
    game: Game,
    record: GameRecord?,
    onPlayClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Continue Playing 🎮",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .clickable { onPlayClick() }
                .testTag("continue_playing_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(game.accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(game.iconEmoji, fontSize = 30.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = game.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Best Score: ${record?.highScore ?: 0}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onPlayClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArcadeGreen)
                ) {
                    Text("PLAY", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DailyChallengeCard(
    challenge: DailyChallenge,
    onPlayChallengeGame: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onPlayChallengeGame() }
            .testTag("daily_challenge_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ArcadeIndigoPrimary.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = challenge.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = ArcadeIndigoPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ArcadeGold.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐ +${challenge.starsReward}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ArcadeGoldDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("⚡ +${challenge.xpReward} XP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ArcadeIndigoPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            val progress = (challenge.currentCount.toFloat() / challenge.targetCount.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ArcadeIndigoPrimary,
                trackColor = ArcadeIndigoPrimary.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${challenge.currentCount} / ${challenge.targetCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tap to Complete →",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ArcadeIndigoPrimary
                )
            }
        }
    }
}

@Composable
fun QuickPlaySection(
    onRandomGame: () -> Unit,
    onReflexTest: () -> Unit,
    onSpeedChallenge: () -> Unit,
    onMemoryTest: () -> Unit
) {
    Column {
        Text(
            text = "Quick Play ⚡",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickPlayPill(emoji = "🎲", label = "Random Game", color = ArcadePurple, onClick = onRandomGame)
            QuickPlayPill(emoji = "⚡", label = "Reflex Test", color = ArcadePink, onClick = onReflexTest)
            QuickPlayPill(emoji = "⏱️", label = "Speed Tap", color = ArcadeCyan, onClick = onSpeedChallenge)
            QuickPlayPill(emoji = "🧠", label = "Memory Test", color = ArcadeGreen, onClick = onMemoryTest)
        }
    }
}

@Composable
fun QuickPlayPill(
    emoji: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
        }
    }
}
