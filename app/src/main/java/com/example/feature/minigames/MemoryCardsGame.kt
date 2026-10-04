package com.example.feature.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

data class MemoryCard(
    val id: Int,
    val emoji: String,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryCardsGame(
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
    var moves by remember { mutableIntStateOf(0) }
    var pairsMatched by remember { mutableIntStateOf(0) }

    val emojis = listOf("🦁", "🐼", "🦊", "🐸", "🤖", "🚀")
    val cards = remember {
        mutableStateListOf<MemoryCard>().apply {
            val doubled = (emojis + emojis).shuffled()
            doubled.forEachIndexed { index, emoji ->
                add(MemoryCard(id = index, emoji = emoji))
            }
        }
    }

    var firstSelectedId by remember { mutableStateOf<Int?>(null) }
    var isCheckingMatch by remember { mutableStateOf(false) }

    fun handleCardClick(card: MemoryCard) {
        if (isPaused || isCheckingMatch || card.isFaceUp || card.isMatched) return

        card.isFaceUp = true
        soundManager.playPop()
        hapticManager.tap()

        if (firstSelectedId == null) {
            firstSelectedId = card.id
        } else {
            moves++
            val firstCard = cards.find { it.id == firstSelectedId }
            if (firstCard != null) {
                if (firstCard.emoji == card.emoji) {
                    // Match found!
                    firstCard.isMatched = true
                    card.isMatched = true
                    combo++
                    if (combo > maxCombo) maxCombo = combo
                    score += 100 * combo
                    starsEarned++
                    pairsMatched++
                    soundManager.playCoin()
                    hapticManager.success()
                    firstSelectedId = null

                    onScoreUpdate(score, combo, starsEarned)

                    if (pairsMatched == emojis.size) {
                        soundManager.playSuccess()
                        onGameOver(score, maxCombo, starsEarned)
                    }
                } else {
                    // No match, flip back
                    isCheckingMatch = true
                    combo = 0
                    onScoreUpdate(score, combo, starsEarned)
                }
            }
        }
    }

    LaunchedEffect(isCheckingMatch) {
        if (isCheckingMatch) {
            delay(850)
            cards.forEach {
                if (!it.isMatched) it.isFaceUp = false
            }
            firstSelectedId = null
            isCheckingMatch = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Pairs: $pairsMatched / ${emojis.size}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ArcadePurple)
            Text("Moves: $moves", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // 4 rows x 3 columns grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        if (index < cards.size) {
                            val card = cards[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(95.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        when {
                                            card.isMatched -> ArcadeGreen.copy(alpha = 0.3f)
                                            card.isFaceUp -> ArcadeIndigoPrimary.copy(alpha = 0.2f)
                                            else -> ArcadeIndigoPrimary
                                        }
                                    )
                                    .clickable { handleCardClick(card) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (card.isFaceUp || card.isMatched) {
                                    Text(card.emoji, fontSize = 36.sp)
                                } else {
                                    Text("⭐", fontSize = 28.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
