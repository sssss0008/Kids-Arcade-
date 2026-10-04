package com.example.feature.minigames

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

data class Balloon(
    val id: Long,
    var x: Float, // 0..1
    var y: Float, // 0..1 (1 is bottom, 0 is top)
    val speed: Float,
    val radius: Float,
    val color: Color,
    val isGolden: Boolean = false,
    val isSpike: Boolean = false,
    val isPopped: Boolean = false
)

data class PopParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    var alpha: Float = 1f
)

@Composable
fun BalloonPopGame(
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

    val balloons = remember { mutableStateListOf<Balloon>() }
    val particles = remember { mutableStateListOf<PopParticle>() }
    var nextId by remember { mutableLongStateOf(0L) }

    // Game loop: timer and spawning
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

    // Animation frame loop for floating balloons and particles
    LaunchedEffect(isPaused) {
        while (!isPaused && timeLeftSeconds > 0) {
            delay(16)

            // Spawn new balloons
            if (balloons.size < 9 && Random.nextFloat() < 0.08f) {
                val isGold = Random.nextFloat() < 0.15f
                val isSpike = !isGold && Random.nextFloat() < 0.12f
                val color = when {
                    isGold -> ArcadeGold
                    isSpike -> Color(0xFF475569)
                    else -> listOf(ArcadePink, ArcadeCyan, ArcadeGreen, ArcadePurple, ArcadeOrange).random()
                }
                balloons.add(
                    Balloon(
                        id = nextId++,
                        x = Random.nextFloat() * 0.8f + 0.1f,
                        y = 1.1f,
                        speed = Random.nextFloat() * 0.005f + 0.004f,
                        radius = if (isGold) 36f else 42f,
                        color = color,
                        isGolden = isGold,
                        isSpike = isSpike
                    )
                )
            }

            // Move balloons upward
            val iterator = balloons.listIterator()
            while (iterator.hasNext()) {
                val b = iterator.next()
                b.y -= b.speed
                if (b.y < -0.1f) {
                    iterator.remove()
                    if (!b.isSpike) {
                        combo = 0 // Drop combo on balloon missed
                        onScoreUpdate(score, combo, starsEarned)
                    }
                }
            }

            // Update particles
            val pIter = particles.listIterator()
            while (pIter.hasNext()) {
                val p = pIter.next()
                p.alpha -= 0.04f
                if (p.alpha <= 0f) {
                    pIter.remove()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isPaused) {
                if (isPaused) return@pointerInput
                detectTapGestures { tapOffset ->
                    val width = size.width
                    val height = size.height

                    // Find tapped balloon
                    val tapped = balloons.findLast { b ->
                        val bx = b.x * width
                        val by = b.y * height
                        hypot(tapOffset.x - bx, tapOffset.y - by) <= (b.radius * 1.6f)
                    }

                    if (tapped != null) {
                        balloons.remove(tapped)
                        // Create pop confetti particles
                        repeat(10) {
                            particles.add(
                                PopParticle(
                                    x = tapped.x * width,
                                    y = tapped.y * height,
                                    vx = (Random.nextFloat() - 0.5f) * 10f,
                                    vy = (Random.nextFloat() - 0.5f) * 10f,
                                    color = tapped.color
                                )
                            )
                        }

                        if (tapped.isSpike) {
                            soundManager.playFailure()
                            hapticManager.failure()
                            combo = 0
                            score = (score - 50).coerceAtLeast(0)
                        } else {
                            soundManager.playPop()
                            hapticManager.pop()
                            combo++
                            if (combo > maxCombo) maxCombo = combo

                            val points = if (tapped.isGolden) 100 * (1 + combo / 3) else 25 * (1 + combo / 4)
                            score += points

                            if (tapped.isGolden) {
                                starsEarned += 2
                                soundManager.playCoin()
                            } else if (combo % 5 == 0) {
                                starsEarned += 1
                                soundManager.playCombo(combo)
                            }
                        }
                        onScoreUpdate(score, combo, starsEarned)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw balloons
            balloons.forEach { b ->
                val center = Offset(b.x * size.width, b.y * size.height)
                // Draw balloon string
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.6f),
                    start = Offset(center.x, center.y + b.radius),
                    end = Offset(center.x + 4f, center.y + b.radius + 26f),
                    strokeWidth = 3f
                )
                // Draw balloon body
                drawCircle(
                    color = b.color,
                    radius = b.radius,
                    center = center
                )
                // Highlight reflection
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f),
                    radius = b.radius * 0.3f,
                    center = Offset(center.x - b.radius * 0.35f, center.y - b.radius * 0.35f)
                )

                if (b.isGolden) {
                    // Small star on golden balloon
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = center
                    )
                } else if (b.isSpike) {
                    // Draw X or spikes on hazard
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x - 12f, center.y - 12f),
                        end = Offset(center.x + 12f, center.y + 12f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x + 12f, center.y - 12f),
                        end = Offset(center.x - 12f, center.y + 12f),
                        strokeWidth = 4f
                    )
                }
            }

            // Draw pop particles
            particles.forEach { p ->
                drawCircle(
                    color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                    radius = 8f * p.alpha,
                    center = Offset(p.x, p.y)
                )
            }
        }

        // Timer badge in corner
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "⏱️ ${timeLeftSeconds}s",
                color = if (timeLeftSeconds <= 5) ArcadePink else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
    }
}
