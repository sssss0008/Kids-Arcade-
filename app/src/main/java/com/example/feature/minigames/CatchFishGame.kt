package com.example.feature.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.random.Random

data class SwimmingCreature(
    val id: Long,
    var x: Float, // 0..1
    val y: Float, // 0..1
    val speed: Float,
    val direction: Float, // 1 = right, -1 = left
    val emoji: String,
    val isGolden: Boolean = false,
    val isHazard: Boolean = false,
    val points: Int = 30
)

@Composable
fun CatchFishGame(
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

    var netX by remember { mutableFloatStateOf(0.5f) } // 0..1
    var netY by remember { mutableFloatStateOf(0.78f) }

    val creatures = remember { mutableStateListOf<SwimmingCreature>() }
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

    // Creature Spawning & Swimming Loop
    LaunchedEffect(isPaused) {
        while (!isPaused && timeLeftSeconds > 0) {
            delay(16)

            if (creatures.size < 7 && Random.nextFloat() < 0.07f) {
                val toRight = Random.nextBoolean()
                val isGold = Random.nextFloat() < 0.15f
                val isHazard = !isGold && Random.nextFloat() < 0.18f

                val emoji = when {
                    isGold -> "🐢"
                    isHazard -> "🐡"
                    else -> listOf("🐠", "🐟", "🐙", "🦀", "🦐").random()
                }

                creatures.add(
                    SwimmingCreature(
                        id = nextId++,
                        x = if (toRight) -0.1f else 1.1f,
                        y = Random.nextFloat() * 0.65f + 0.15f,
                        speed = Random.nextFloat() * 0.006f + 0.004f,
                        direction = if (toRight) 1f else -1f,
                        emoji = emoji,
                        isGolden = isGold,
                        isHazard = isHazard,
                        points = if (isGold) 100 else 35
                    )
                )
            }

            // Move creatures
            val iter = creatures.listIterator()
            while (iter.hasNext()) {
                val c = iter.next()
                c.x += c.speed * c.direction
                if ((c.direction > 0 && c.x > 1.2f) || (c.direction < 0 && c.x < -0.2f)) {
                    iter.remove()
                }
            }

            // Check collision with net
            val netRadius = 0.12f
            val caughtIter = creatures.listIterator()
            while (caughtIter.hasNext()) {
                val c = caughtIter.next()
                val dx = c.x - netX
                val dy = c.y - netY
                if (dx * dx + dy * dy < (netRadius * netRadius)) {
                    caughtIter.remove()
                    if (c.isHazard) {
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
                        } else if (combo % 4 == 0) {
                            starsEarned += 1
                            soundManager.playCombo(combo)
                        }
                    }
                    onScoreUpdate(score, combo, starsEarned)
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
                    netX = (netX + dragAmount.x / size.width).coerceIn(0.1f, 0.9f)
                    netY = (netY + dragAmount.y / size.height).coerceIn(0.2f, 0.88f)
                }
            }
            .pointerInput(isPaused) {
                if (isPaused) return@pointerInput
                detectTapGestures { tapOffset ->
                    netX = (tapOffset.x / size.width).coerceIn(0.1f, 0.9f)
                    netY = (tapOffset.y / size.height).coerceIn(0.2f, 0.88f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Ocean gradient background tint
            drawRect(
                color = ArcadeTeal.copy(alpha = 0.08f),
                size = size
            )

            // Net Ring & Mesh
            val nx = netX * size.width
            val ny = netY * size.height
            val r = 50f

            // Net handle
            drawLine(
                color = ArcadeAmber,
                start = Offset(nx, ny + r),
                end = Offset(nx, ny + r + 50f),
                strokeWidth = 8f
            )

            // Net bag
            drawCircle(
                color = ArcadeCyan.copy(alpha = 0.25f),
                radius = r,
                center = Offset(nx, ny)
            )
            drawCircle(
                color = ArcadeCyanLight,
                radius = r,
                center = Offset(nx, ny),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f)
            )
        }

        // Creatures rendered on top
        creatures.forEach { c ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(
                        x = (c.x * 340).dp,
                        y = (c.y * 580).dp
                    )
            ) {
                Text(
                    text = c.emoji,
                    fontSize = if (c.isGolden) 36.sp else 30.sp
                )
            }
        }

        // Timer badge
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "⏱️ ${timeLeftSeconds}s  •  Drag Net 🕸️",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = if (timeLeftSeconds <= 5) ArcadePink else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
