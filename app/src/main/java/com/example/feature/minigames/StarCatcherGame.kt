package com.example.feature.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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

data class CelestialStar(
    val id: Long,
    val x: Float, // 0..1
    var y: Float, // 0..1
    val speed: Float,
    val isRainbow: Boolean,
    val isStorm: Boolean,
    val points: Int,
    var twinkle: Float = 0f
)

@Composable
fun StarCatcherGame(
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

    val stars = remember { mutableStateListOf<CelestialStar>() }
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

    // Star fall loop
    LaunchedEffect(isPaused) {
        while (!isPaused && timeLeftSeconds > 0) {
            delay(16)

            // Spawn stars
            if (stars.size < 8 && Random.nextFloat() < 0.09f) {
                val isRainbow = Random.nextFloat() < 0.15f
                val isStorm = !isRainbow && Random.nextFloat() < 0.16f

                stars.add(
                    CelestialStar(
                        id = nextId++,
                        x = Random.nextFloat() * 0.82f + 0.09f,
                        y = -0.05f,
                        speed = Random.nextFloat() * 0.005f + 0.004f,
                        isRainbow = isRainbow,
                        isStorm = isStorm,
                        points = if (isRainbow) 120 else 40
                    )
                )
            }

            // Move stars
            val iter = stars.listIterator()
            while (iter.hasNext()) {
                val s = iter.next()
                s.y += s.speed
                s.twinkle += 0.08f
                if (s.y > 1.05f) {
                    iter.remove()
                    if (!s.isStorm) {
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
                detectTapGestures { tapOffset ->
                    val width = size.width
                    val height = size.height

                    val tapped = stars.findLast { s ->
                        val sx = s.x * width
                        val sy = s.y * height
                        hypot(tapOffset.x - sx, tapOffset.y - sy) <= 65f
                    }

                    if (tapped != null) {
                        stars.remove(tapped)
                        if (tapped.isStorm) {
                            soundManager.playFailure()
                            hapticManager.failure()
                            combo = 0
                            score = (score - 50).coerceAtLeast(0)
                        } else {
                            soundManager.playCoin()
                            hapticManager.pop()
                            combo++
                            if (combo > maxCombo) maxCombo = combo
                            score += tapped.points * (1 + combo / 3)

                            starsEarned += if (tapped.isRainbow) 3 else 1
                            if (combo % 5 == 0) {
                                soundManager.playCombo(combo)
                            }
                        }
                        onScoreUpdate(score, combo, starsEarned)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { s ->
                val center = Offset(s.x * size.width, s.y * size.height)
                val color = when {
                    s.isRainbow -> ArcadePink
                    s.isStorm -> Color(0xFF64748B)
                    else -> ArcadeGold
                }

                // Glowing aura
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = 28f,
                    center = center
                )
                // Center Star Core
                drawCircle(
                    color = color,
                    radius = 16f,
                    center = center
                )
            }
        }

        // Emoji overlay
        stars.forEach { s ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(
                        x = (s.x * 330).dp,
                        y = (s.y * 580).dp
                    )
            ) {
                Text(
                    text = if (s.isStorm) "🌩️" else if (s.isRainbow) "🌟" else "⭐",
                    fontSize = if (s.isRainbow) 34.sp else 28.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "⏱️ ${timeLeftSeconds}s  •  Catch Starlight ✨",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = if (timeLeftSeconds <= 5) ArcadePink else Color.White
            )
        }
    }
}
