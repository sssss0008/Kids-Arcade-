package com.example.feature.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

data class FallingCandy(
    val id: Long,
    var x: Float, // 0..1
    var y: Float, // 0..1
    val speed: Float,
    val emoji: String,
    val isGolden: Boolean = false,
    val isBad: Boolean = false,
    val points: Int = 30
)

@Composable
fun CandyCatcherGame(
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
    var timeLeftSeconds by remember { mutableIntStateOf(35) }

    var basketX by remember { mutableFloatStateOf(0.5f) } // 0..1
    val basketY = 0.85f
    val basketWidth = 0.26f

    val candies = remember { mutableStateListOf<FallingCandy>() }
    var nextId by remember { mutableLongStateOf(0L) }

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

    // Candy Spawning & Falling Loop
    LaunchedEffect(isPaused) {
        while (!isPaused && timeLeftSeconds > 0) {
            delay(16)

            if (candies.size < 7 && Random.nextFloat() < 0.09f) {
                val isGold = Random.nextFloat() < 0.15f
                val isBad = !isGold && Random.nextFloat() < 0.18f
                val emoji = when {
                    isGold -> "🧁"
                    isBad -> "🥦"
                    else -> listOf("🍬", "🍭", "🍫", "🍩", "🍪").random()
                }

                candies.add(
                    FallingCandy(
                        id = nextId++,
                        x = Random.nextFloat() * 0.8f + 0.1f,
                        y = -0.05f,
                        speed = Random.nextFloat() * 0.007f + 0.005f,
                        emoji = emoji,
                        isGolden = isGold,
                        isBad = isBad,
                        points = if (isGold) 120 else 40
                    )
                )
            }

            // Move candies down
            val iter = candies.listIterator()
            while (iter.hasNext()) {
                val c = iter.next()
                c.y += c.speed

                // Catch check
                if (c.y in (basketY - 0.06f)..(basketY + 0.06f)) {
                    if (abs(c.x - basketX) < (basketWidth / 2f + 0.04f)) {
                        iter.remove()
                        if (c.isBad) {
                            soundManager.playFailure()
                            hapticManager.failure()
                            combo = 0
                            score = (score - 60).coerceAtLeast(0)
                        } else {
                            soundManager.playPop()
                            hapticManager.pop()
                            combo++
                            if (combo > maxCombo) maxCombo = combo
                            score += c.points * (1 + combo / 3)

                            if (c.isGolden) {
                                starsEarned += 2
                                soundManager.playCoin()
                            } else if (combo % 5 == 0) {
                                starsEarned++
                                soundManager.playCombo(combo)
                            }
                        }
                        onScoreUpdate(score, combo, starsEarned)
                        continue
                    }
                }

                // Fallen past screen
                if (c.y > 1.05f) {
                    iter.remove()
                    if (!c.isBad) {
                        combo = 0
                        onScoreUpdate(score, combo, starsEarned)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isPaused) {
                if (isPaused) return@pointerInput
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    basketX = (basketX + dragAmount.x / size.width).coerceIn(0.12f, 0.88f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw candy basket
            val bx = basketX * size.width
            val by = basketY * size.height
            val bw = basketWidth * size.width
            val bh = 34f

            drawRoundRect(
                color = ArcadePink,
                topLeft = Offset(bx - bw / 2, by),
                size = Size(bw, bh),
                cornerRadius = CornerRadius(16f, 16f)
            )
            // Basket weave accents
            drawRoundRect(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = Offset(bx - bw / 2 + 10f, by + 6f),
                size = Size(bw - 20f, 6f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Falling candies
        candies.forEach { c ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(
                        x = (c.x * 330).dp,
                        y = (c.y * 580).dp
                    )
            ) {
                Text(c.emoji, fontSize = if (c.isGolden) 36.sp else 30.sp)
            }
        }

        // Timer badge
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "⏱️ ${timeLeftSeconds}s  •  Slide Basket 🍬",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = if (timeLeftSeconds <= 5) ArcadePink else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
