package com.example.feature.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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

data class LilyPad(
    val row: Int,
    var x: Float, // center 0..1
    val width: Float,
    val speed: Float,
    var direction: Float,
    val hasStar: Boolean = false
)

@Composable
fun JumpingFrogGame(
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
    var frogRow by remember { mutableIntStateOf(0) } // 0 is bottom start pad
    var frogX by remember { mutableFloatStateOf(0.5f) }
    var isJumping by remember { mutableStateOf(false) }

    // Rows of pads: row 0 (start), 1, 2, 3, 4
    val pads = remember {
        mutableStateListOf(
            LilyPad(row = 0, x = 0.5f, width = 0.35f, speed = 0f, direction = 0f),
            LilyPad(row = 1, x = 0.4f, width = 0.28f, speed = 0.006f, direction = 1f, hasStar = true),
            LilyPad(row = 2, x = 0.6f, width = 0.25f, speed = 0.008f, direction = -1f),
            LilyPad(row = 3, x = 0.3f, width = 0.22f, speed = 0.010f, direction = 1f, hasStar = true),
            LilyPad(row = 4, x = 0.5f, width = 0.20f, speed = 0.012f, direction = -1f, hasStar = true)
        )
    }

    // Pad motion loop
    LaunchedEffect(isPaused) {
        while (!isPaused) {
            delay(16)
            pads.forEach { p ->
                if (p.speed > 0f) {
                    p.x += p.speed * p.direction
                    if (p.x > 0.85f) {
                        p.x = 0.85f
                        p.direction = -1f
                    } else if (p.x < 0.15f) {
                        p.x = 0.15f
                        p.direction = 1f
                    }
                }
            }
            // Move frog with current pad if standing on it
            val curPad = pads.find { it.row == frogRow }
            if (curPad != null && !isJumping) {
                frogX = curPad.x
            }
        }
    }

    fun handleJump() {
        if (isJumping || isPaused) return
        isJumping = true
        soundManager.playJump()
        hapticManager.tap()

        val nextRow = frogRow + 1
        val targetPad = pads.find { it.row == nextRow }

        if (targetPad != null) {
            // Check if frog landed within targetPad width
            val landingDistance = abs(frogX - targetPad.x)
            val halfWidth = targetPad.width / 2f

            if (landingDistance <= halfWidth) {
                // Landed!
                frogRow = nextRow
                distance++
                val isPerfect = landingDistance < (halfWidth * 0.35f)
                if (isPerfect) {
                    combo++
                    if (combo > maxCombo) maxCombo = combo
                    score += 50 * combo
                    soundManager.playCombo(combo)
                } else {
                    combo = 1
                    score += 30
                }

                if (targetPad.hasStar) {
                    starsEarned++
                    soundManager.playCoin()
                }

                onScoreUpdate(score, combo, starsEarned)

                // If reached top row, scroll down by resetting rows with new speeds
                if (frogRow >= 4) {
                    score += 150
                    soundManager.playSuccess()
                    pads[0] = pads[4].copy(row = 0, speed = 0f)
                    pads[1] = LilyPad(1, Random.nextFloat() * 0.6f + 0.2f, 0.26f, 0.007f + distance * 0.001f, 1f, true)
                    pads[2] = LilyPad(2, Random.nextFloat() * 0.6f + 0.2f, 0.24f, 0.009f + distance * 0.001f, -1f)
                    pads[3] = LilyPad(3, Random.nextFloat() * 0.6f + 0.2f, 0.22f, 0.011f + distance * 0.001f, 1f, true)
                    pads[4] = LilyPad(4, Random.nextFloat() * 0.6f + 0.2f, 0.20f, 0.013f + distance * 0.001f, -1f, true)
                    frogRow = 0
                }
            } else {
                // Missed pad, splash into pond!
                soundManager.playFailure()
                hapticManager.failure()
                onGameOver(score, maxCombo, starsEarned)
            }
        }
        isJumping = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isPaused) {
                if (isPaused) return@pointerInput
                detectTapGestures {
                    handleJump()
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Water ripples
            drawRect(color = Color(0xFF0284C7).copy(alpha = 0.15f), size = size)

            // Draw Lily Pads
            pads.forEach { p ->
                val padY = size.height * (0.85f - p.row * 0.18f)
                val padWidthPx = p.width * size.width
                val padHeightPx = 36f
                val left = (p.x * size.width) - (padWidthPx / 2f)

                // Pad body
                drawRoundRect(
                    color = ArcadeGreen,
                    topLeft = Offset(left, padY - (padHeightPx / 2f)),
                    size = Size(padWidthPx, padHeightPx),
                    cornerRadius = CornerRadius(18f, 18f)
                )
                // Lily center vein
                drawLine(
                    color = ArcadeGreenLight,
                    start = Offset(left + 15f, padY),
                    end = Offset(left + padWidthPx - 15f, padY),
                    strokeWidth = 3f
                )

                // Star on pad
                if (p.hasStar) {
                    drawCircle(
                        color = ArcadeGold,
                        radius = 8f,
                        center = Offset(p.x * size.width, padY - 20f)
                    )
                }
            }
        }

        // Frog character
        val frogYPercent = 0.85f - frogRow * 0.18f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = (frogX * 320).dp,
                    y = (frogYPercent * 580).dp
                )
        ) {
            Text(
                text = "🐸",
                fontSize = 38.sp
            )
        }

        // Tap Jump Button on bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { handleJump() },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcadeGreen)
            ) {
                Text("TAP TO HOP 🐸", fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Distance: ${distance}m", fontWeight = FontWeight.Bold, color = ArcadeGoldDark)
        }
    }
}
