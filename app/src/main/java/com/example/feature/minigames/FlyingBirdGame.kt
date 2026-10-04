package com.example.feature.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

data class CloudGate(
    var x: Float, // 0..1
    val gapY: Float, // center of gap (0.25..0.75)
    val gapHeight: Float = 0.30f,
    var passed: Boolean = false,
    val hasStar: Boolean = true
)

@Composable
fun FlyingBirdGame(
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
    var distance by remember { mutableIntStateOf(0) }

    var birdY by remember { mutableFloatStateOf(0.45f) }
    var birdVelocity by remember { mutableFloatStateOf(0f) }

    val gates = remember {
        mutableStateListOf(
            CloudGate(x = 1.0f, gapY = 0.45f),
            CloudGate(x = 1.6f, gapY = 0.55f),
            CloudGate(x = 2.2f, gapY = 0.40f)
        )
    }

    fun flap() {
        if (isPaused) return
        birdVelocity = -0.018f
        soundManager.playJump()
        hapticManager.tap()
    }

    // Physics & Gates Loop
    LaunchedEffect(isPaused) {
        while (!isPaused) {
            delay(16)

            // Gravity
            birdVelocity += 0.0009f
            birdY += birdVelocity

            // Check ground or ceiling hit
            if (birdY > 0.92f || birdY < 0.05f) {
                soundManager.playFailure()
                hapticManager.failure()
                onGameOver(score, maxCombo, starsEarned)
                return@LaunchedEffect
            }

            // Move gates left
            gates.forEach { g ->
                g.x -= 0.0055f

                // Bird collision check with cloud gates (bird is at x = 0.25)
                val birdX = 0.25f
                if (g.x in (birdX - 0.08f)..(birdX + 0.08f)) {
                    val inGap = birdY in (g.gapY - g.gapHeight / 2f)..(g.gapY + g.gapHeight / 2f)
                    if (!inGap) {
                        // Collided with fluffy cloud!
                        soundManager.playFailure()
                        hapticManager.failure()
                        onGameOver(score, maxCombo, starsEarned)
                        return@LaunchedEffect
                    }
                }

                // Check passed gate
                if (!g.passed && g.x < birdX) {
                    g.passed = true
                    distance++
                    combo++
                    if (combo > maxCombo) maxCombo = combo
                    score += 50 * combo
                    if (g.hasStar) {
                        starsEarned++
                        soundManager.playCoin()
                    } else {
                        soundManager.playPop()
                    }
                    onScoreUpdate(score, combo, starsEarned)
                }

                // Recycle gate to right
                if (g.x < -0.2f) {
                    g.x = 1.6f
                    g.passed = false
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isPaused) {
                if (isPaused) return@pointerInput
                detectTapGestures { flap() }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Sky backdrop
            drawRect(color = ArcadeCyanLight.copy(alpha = 0.12f), size = size)

            // Gates (Cloud Pillars)
            gates.forEach { g ->
                val gx = g.x * size.width
                val gateWidth = 50f
                val gapCenterY = g.gapY * size.height
                val halfGap = (g.gapHeight * size.height) / 2f

                // Top cloud pillar
                drawRoundRect(
                    color = ArcadeIndigoPrimary.copy(alpha = 0.35f),
                    topLeft = Offset(gx - gateWidth / 2, 0f),
                    size = Size(gateWidth, gapCenterY - halfGap),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )

                // Bottom cloud pillar
                drawRoundRect(
                    color = ArcadeIndigoPrimary.copy(alpha = 0.35f),
                    topLeft = Offset(gx - gateWidth / 2, gapCenterY + halfGap),
                    size = Size(gateWidth, size.height - (gapCenterY + halfGap)),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )

                // Star in middle of gap
                if (g.hasStar && !g.passed) {
                    drawCircle(
                        color = ArcadeGold,
                        radius = 12f,
                        center = Offset(gx, gapCenterY)
                    )
                }
            }
        }

        // Flying Bird
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = 80.dp,
                    y = (birdY * 600).dp
                )
        ) {
            Text(
                text = "🐦",
                fontSize = 38.sp
            )
        }

        // Bottom Flap Hint
        Button(
            onClick = { flap() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.7f)
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ArcadeIndigoPrimary)
        ) {
            Text("TAP TO FLAP 🪽", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
