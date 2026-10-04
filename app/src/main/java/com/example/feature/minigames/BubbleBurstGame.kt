package com.example.feature.minigames

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
import androidx.compose.ui.graphics.drawscope.Stroke
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

data class Bubble(
    val id: Long,
    var x: Float, // 0..1
    var y: Float, // 0..1
    var radius: Float,
    val maxRadius: Float,
    val color: Color,
    val isSuperBubble: Boolean = false,
    var life: Float = 1.0f // 1 -> 0
)

data class BubbleRipple(
    val x: Float,
    val y: Float,
    var radius: Float,
    val maxRadius: Float,
    val color: Color,
    var alpha: Float = 1f
)

@Composable
fun BubbleBurstGame(
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
    var timeLeftSeconds by remember { mutableIntStateOf(30) }

    val bubbles = remember { mutableStateListOf<Bubble>() }
    val ripples = remember { mutableStateListOf<BubbleRipple>() }
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

    // Animation & Spawning Loop
    LaunchedEffect(isPaused) {
        while (!isPaused && timeLeftSeconds > 0) {
            delay(16)

            // Spawn bubbles
            if (bubbles.size < 12 && Random.nextFloat() < 0.12f) {
                val isSuper = Random.nextFloat() < 0.12f
                val baseRadius = if (isSuper) 55f else Random.nextFloat() * 30f + 25f
                bubbles.add(
                    Bubble(
                        id = nextId++,
                        x = Random.nextFloat() * 0.8f + 0.1f,
                        y = Random.nextFloat() * 0.75f + 0.12f,
                        radius = 5f,
                        maxRadius = baseRadius,
                        color = if (isSuper) ArcadeGold else listOf(ArcadeCyan, ArcadePinkLight, ArcadeGreenLight, ArcadePurple).random(),
                        isSuperBubble = isSuper
                    )
                )
            }

            // Grow and age bubbles
            val bIter = bubbles.listIterator()
            while (bIter.hasNext()) {
                val b = bIter.next()
                if (b.radius < b.maxRadius) {
                    b.radius += 1.5f
                }
                b.life -= 0.007f
                b.y -= 0.001f // gentle float
                if (b.life <= 0f) {
                    bIter.remove()
                    combo = 0
                    onScoreUpdate(score, combo, starsEarned)
                }
            }

            // Expand ripples
            val rIter = ripples.listIterator()
            while (rIter.hasNext()) {
                val r = rIter.next()
                r.radius += 4f
                r.alpha -= 0.05f
                if (r.alpha <= 0f) {
                    rIter.remove()
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

                    val tapped = bubbles.findLast { b ->
                        val bx = b.x * width
                        val by = b.y * height
                        hypot(tapOffset.x - bx, tapOffset.y - by) <= (b.radius * 1.5f)
                    }

                    if (tapped != null) {
                        bubbles.remove(tapped)
                        ripples.add(
                            BubbleRipple(
                                x = tapped.x * width,
                                y = tapped.y * height,
                                radius = tapped.radius,
                                maxRadius = tapped.radius * 2.2f,
                                color = tapped.color
                            )
                        )

                        soundManager.playPop()
                        hapticManager.pop()
                        combo++
                        if (combo > maxCombo) maxCombo = combo

                        val sizeMultiplier = if (tapped.radius < 35f) 2 else 1
                        val points = (30 * sizeMultiplier) * (1 + combo / 3)
                        score += points

                        // Chain reaction if super bubble
                        if (tapped.isSuperBubble) {
                            soundManager.playCoin()
                            starsEarned += 2
                            // Burst nearby bubbles in chain
                            val nearby = bubbles.filter { other ->
                                val ox = other.x * width
                                val oy = other.y * height
                                hypot(tapOffset.x - ox, tapOffset.y - oy) < 220f
                            }
                            nearby.forEach { n ->
                                bubbles.remove(n)
                                score += 50
                                ripples.add(BubbleRipple(n.x * width, n.y * height, n.radius, n.radius * 2f, n.color))
                            }
                        }

                        if (combo % 6 == 0) {
                            starsEarned++
                            soundManager.playCombo(combo)
                        }

                        onScoreUpdate(score, combo, starsEarned)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw ripples
            ripples.forEach { r ->
                drawCircle(
                    color = r.color.copy(alpha = r.alpha.coerceIn(0f, 1f)),
                    radius = r.radius,
                    center = Offset(r.x, r.y),
                    style = Stroke(width = 4f)
                )
            }

            // Draw bubbles
            bubbles.forEach { b ->
                val center = Offset(b.x * size.width, b.y * size.height)
                val alpha = b.life.coerceIn(0.2f, 1f)

                // Translucent bubble body
                drawCircle(
                    color = b.color.copy(alpha = 0.35f * alpha),
                    radius = b.radius,
                    center = center
                )
                // Crisp bubble rim
                drawCircle(
                    color = b.color.copy(alpha = 0.85f * alpha),
                    radius = b.radius,
                    center = center,
                    style = Stroke(width = 3.5f)
                )
                // Shimmer reflection highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.7f * alpha),
                    radius = b.radius * 0.28f,
                    center = Offset(center.x - b.radius * 0.35f, center.y - b.radius * 0.35f)
                )

                if (b.isSuperBubble) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f),
                        radius = 8f,
                        center = center
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "⏱️ ${timeLeftSeconds}s",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = if (timeLeftSeconds <= 5) ArcadePink else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
