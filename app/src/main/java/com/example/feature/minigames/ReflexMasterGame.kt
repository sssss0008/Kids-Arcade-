package com.example.feature.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class ReflexState {
    WAITING_TO_START,
    WAIT_FOR_GREEN,
    TAP_NOW,
    TOO_EARLY,
    ROUND_RESULT
}

@Composable
fun ReflexMasterGame(
    isPaused: Boolean,
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager,
    onScoreUpdate: (score: Int, combo: Int, stars: Int) -> Unit,
    onGameOver: (finalScore: Int, maxCombo: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(ReflexState.WAITING_TO_START) }
    var currentRound by remember { mutableIntStateOf(1) }
    val totalRounds = 5

    var reactionTimes = remember { mutableStateListOf<Long>() }
    var lastReactionTime by remember { mutableLongStateOf(0L) }
    var greenTriggerTime by remember { mutableLongStateOf(0L) }

    var score by remember { mutableIntStateOf(0) }
    var starsEarned by remember { mutableIntStateOf(0) }

    fun startRound() {
        state = ReflexState.WAIT_FOR_GREEN
    }

    LaunchedEffect(state) {
        if (state == ReflexState.WAIT_FOR_GREEN) {
            val randomWait = Random.nextLong(1500, 3800)
            delay(randomWait)
            if (state == ReflexState.WAIT_FOR_GREEN) {
                greenTriggerTime = System.currentTimeMillis()
                state = ReflexState.TAP_NOW
                soundManager.playPop()
                hapticManager.pop()
            }
        }
    }

    fun handleScreenTap() {
        if (isPaused) return
        when (state) {
            ReflexState.WAITING_TO_START -> {
                startRound()
            }
            ReflexState.WAIT_FOR_GREEN -> {
                // Too early!
                state = ReflexState.TOO_EARLY
                soundManager.playFailure()
                hapticManager.failure()
            }
            ReflexState.TAP_NOW -> {
                val reactionMs = System.currentTimeMillis() - greenTriggerTime
                lastReactionTime = reactionMs
                reactionTimes.add(reactionMs)

                val points = (1000 - reactionMs).coerceAtLeast(50).toInt()
                score += points
                if (reactionMs < 300) {
                    starsEarned += 2
                    soundManager.playCoin()
                } else {
                    starsEarned += 1
                    soundManager.playSuccess()
                }
                hapticManager.success()

                onScoreUpdate(score, 1, starsEarned)
                state = ReflexState.ROUND_RESULT

                if (currentRound >= totalRounds) {
                    onGameOver(score, 1, starsEarned)
                }
            }
            ReflexState.TOO_EARLY, ReflexState.ROUND_RESULT -> {
                if (currentRound < totalRounds) {
                    currentRound++
                    startRound()
                }
            }
        }
    }

    val bgColor = when (state) {
        ReflexState.WAIT_FOR_GREEN -> ArcadePink
        ReflexState.TAP_NOW -> ArcadeGreen
        ReflexState.TOO_EARLY -> ArcadeOrange
        ReflexState.ROUND_RESULT -> ArcadeIndigoPrimary
        ReflexState.WAITING_TO_START -> MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .clickable { handleScreenTap() }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Round $currentRound of $totalRounds",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (state) {
                ReflexState.WAITING_TO_START -> {
                    Text("⚡", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Reflex Master",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When the screen turns GREEN, tap as fast as you can!\n\nTap to start",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                ReflexState.WAIT_FOR_GREEN -> {
                    Text("⏳", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "WAIT FOR GREEN...",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                ReflexState.TAP_NOW -> {
                    Text("💥", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "TAP NOW!",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                ReflexState.TOO_EARLY -> {
                    Text("⚠️", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Too Early!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to retry this round",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 16.sp
                    )
                }
                ReflexState.ROUND_RESULT -> {
                    Text("⚡", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${lastReactionTime} ms",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = ArcadeGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when {
                            lastReactionTime < 250 -> "Incredible Lightning Reflexes! 🚀"
                            lastReactionTime < 350 -> "Super Fast! ⭐"
                            else -> "Good Effort! Keep practicing! 🎮"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Tap for next round →", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
