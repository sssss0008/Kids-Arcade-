package com.example.feature.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

data class MapTile(
    val row: Int,
    val col: Int,
    var item: String = "", // "", "🪙", "💎", "🗝️", "📦", "⭐", "🪨"
    var isRevealed: Boolean = false
)

@Composable
fun TreasureCollectorGame(
    isPaused: Boolean,
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager,
    onScoreUpdate: (score: Int, combo: Int, stars: Int) -> Unit,
    onGameOver: (finalScore: Int, maxCombo: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var starsEarned by remember { mutableIntStateOf(0) }
    var keysCollected by remember { mutableIntStateOf(0) }
    var timeLeftSeconds by remember { mutableIntStateOf(40) }

    var playerRow by remember { mutableIntStateOf(2) }
    var playerCol by remember { mutableIntStateOf(2) }

    val gridSize = 5
    val grid = remember {
        mutableStateListOf<MapTile>().apply {
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    val item = when {
                        r == 2 && c == 2 -> "" // Start position
                        Random.nextFloat() < 0.12f -> "🗝️"
                        Random.nextFloat() < 0.15f -> "📦"
                        Random.nextFloat() < 0.20f -> "💎"
                        Random.nextFloat() < 0.25f -> "🪙"
                        Random.nextFloat() < 0.10f -> "⭐"
                        Random.nextFloat() < 0.10f -> "🪨" // rock obstacle
                        else -> ""
                    }
                    add(MapTile(r, c, item, isRevealed = true))
                }
            }
        }
    }

    // Timer loop
    LaunchedEffect(isPaused) {
        while (!isPaused && timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        }
        if (timeLeftSeconds <= 0) {
            soundManager.playSuccess()
            onGameOver(score, maxCombo, starsEarned)
        }
    }

    fun stepTo(r: Int, c: Int) {
        if (isPaused) return
        // Must be adjacent (up, down, left, right)
        val isAdjacent = (abs(r - playerRow) + abs(c - playerCol)) == 1
        if (!isAdjacent) return

        val tile = grid.find { it.row == r && it.col == c } ?: return
        if (tile.item == "🪨") {
            soundManager.playFailure()
            hapticManager.tap()
            return
        }

        playerRow = r
        playerCol = c
        hapticManager.tap()

        when (tile.item) {
            "🪙" -> {
                soundManager.playCoin()
                combo++
                if (combo > maxCombo) maxCombo = combo
                score += 30 * combo
                tile.item = ""
            }
            "💎" -> {
                soundManager.playPop()
                combo++
                if (combo > maxCombo) maxCombo = combo
                score += 80 * combo
                tile.item = ""
            }
            "⭐" -> {
                soundManager.playCoin()
                starsEarned++
                score += 100
                tile.item = ""
            }
            "🗝️" -> {
                soundManager.playSuccess()
                keysCollected++
                score += 50
                tile.item = ""
            }
            "📦" -> {
                if (keysCollected > 0) {
                    keysCollected--
                    soundManager.playSuccess()
                    combo += 2
                    if (combo > maxCombo) maxCombo = combo
                    score += 250
                    starsEarned += 2
                    tile.item = "✨"
                } else {
                    soundManager.playFailure()
                    // Needs key!
                }
            }
        }
        onScoreUpdate(score, combo, starsEarned)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Status Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🗝️ Keys: $keysCollected",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ArcadeGoldDark
            )
            Text(
                text = "⏱️ ${timeLeftSeconds}s",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = if (timeLeftSeconds <= 5) ArcadePink else MaterialTheme.colorScheme.onSurface
            )
        }

        // Map Grid
        Box(
            modifier = Modifier
                .size(320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(ArcadeGold.copy(alpha = 0.15f))
                .border(3.dp, ArcadeGold, RoundedCornerShape(24.dp))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (r in 0 until gridSize) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (c in 0 until gridSize) {
                            val tile = grid.find { it.row == r && it.col == c }
                            val isPlayer = (r == playerRow && c == playerCol)

                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isPlayer) ArcadeIndigoPrimary.copy(alpha = 0.35f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable { stepTo(r, c) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPlayer) {
                                    Text("🤠", fontSize = 28.sp)
                                } else if (tile != null && tile.item.isNotEmpty()) {
                                    Text(tile.item, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tap adjacent tiles to move explorer 🗺️",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
