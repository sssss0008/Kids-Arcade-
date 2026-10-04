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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun MonkeySwingGame(
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
    var swingsCount by remember { mutableIntStateOf(0) }

    // Pendulum swing angle
    var swingAngle by remember { mutableFloatStateOf(0f) }
    var angleSpeed by remember { mutableFloatStateOf(0.06f) }
    var isJumpingToNextVine by remember { mutableStateOf(false) }

    // Floating bananas
    val hasBanana = remember { mutableStateOf(true) }

    fun releaseAndLeap() {
        if (isPaused || isJumpingToNextVine) return
        isJumpingToNextVine = true
        soundManager.playJump()
        hapticManager.tap()

        // Successful leap if releasing near forward swing (angle between 0.3 and 1.2 radians)
        if (swingAngle > 0.2f && swingAngle < 1.3f) {
            swingsCount++
            combo++
            if (combo > maxCombo) maxCombo = combo
            score += 60 * combo
            soundManager.playCombo(combo)

            if (hasBanana.value) {
                starsEarned++
                soundManager.playCoin()
                hasBanana.value = false
            }

            onScoreUpdate(score, combo, starsEarned)

            // Next vine
            swingAngle = -1.1f
            angleSpeed = (0.06f + swingsCount * 0.003f).coerceAtMost(0.12f)
            hasBanana.value = Random.nextBoolean()
        } else {
            // Bad timing! Fall into the jungle canopy
            soundManager.playFailure()
            hapticManager.failure()
            onGameOver(score, maxCombo, starsEarned)
        }
        isJumpingToNextVine = false
    }

    // Pendulum loop
    LaunchedEffect(isPaused) {
        var direction = 1f
        while (!isPaused) {
            delay(16)
            swingAngle += angleSpeed * direction
            if (swingAngle > 1.2f) {
                swingAngle = 1.2f
                direction = -1f
            } else if (swingAngle < -1.2f) {
                swingAngle = -1.2f
                direction = 1f
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isPaused) {
                if (isPaused) return@pointerInput
                detectTapGestures { releaseAndLeap() }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val anchorX = size.width * 0.45f
            val anchorY = 60f
            val vineLength = size.height * 0.45f

            // Calculate monkey position on vine pendulum
            val monkeyX = anchorX + vineLength * sin(swingAngle)
            val monkeyY = anchorY + vineLength * cos(swingAngle)

            // Draw Vine
            drawLine(
                color = ArcadeGreenDark,
                start = Offset(anchorX, anchorY),
                end = Offset(monkeyX, monkeyY),
                strokeWidth = 6f
            )

            // Next vine in distance (target)
            val nextAnchorX = size.width * 0.85f
            drawLine(
                color = ArcadeGreenDark.copy(alpha = 0.5f),
                start = Offset(nextAnchorX, anchorY),
                end = Offset(nextAnchorX, anchorY + vineLength * 0.8f),
                strokeWidth = 4f
            )

            // Target banana in middle air
            drawCircle(
                color = ArcadeGold,
                radius = 16f,
                center = Offset(size.width * 0.65f, size.height * 0.38f)
            )
        }

        // Monkey graphic
        val anchorX = 160f
        val vineLength = 260f
        val mx = anchorX + vineLength * sin(swingAngle)
        val my = 60f + vineLength * cos(swingAngle)

        Box(
            modifier = Modifier
                .offset(
                    x = (mx * 0.85f).dp,
                    y = (my * 0.85f).dp
                )
        ) {
            Text("🐒", fontSize = 38.sp)
        }

        // Tap Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { releaseAndLeap() },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcadeAmber)
            ) {
                Text("SWING & LEAP! 🍌", fontWeight = FontWeight.Black, fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Vines Crossed: $swingsCount", fontWeight = FontWeight.Bold, color = ArcadeAmber)
        }
    }
}

val ArcadeGreenDark = Color(0xFF047857)
