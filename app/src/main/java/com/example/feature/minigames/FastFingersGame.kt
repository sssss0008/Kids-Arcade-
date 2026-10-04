package com.example.feature.minigames

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun FastFingersGame(
    isPaused: Boolean,
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager,
    onScoreUpdate: (score: Int, combo: Int, stars: Int) -> Unit,
    onGameOver: (finalScore: Int, maxCombo: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableIntStateOf(0) }
    var tapsCount by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var starsEarned by remember { mutableIntStateOf(0) }
    var timeLeftSeconds by remember { mutableIntStateOf(15) }

    var buttonOffsetX by remember { mutableFloatStateOf(0f) }
    var buttonOffsetY by remember { mutableFloatStateOf(0f) }
    var isTappedAnim by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isTappedAnim) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "tap_button_scale"
    )

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

    fun handleRapidTap() {
        if (isPaused) return
        tapsCount++
        combo++
        if (combo > maxCombo) maxCombo = combo
        score += 25 * (1 + combo / 10)

        soundManager.playPop()
        hapticManager.pop()
        isTappedAnim = true

        // Move target slightly to test accuracy
        if (tapsCount % 4 == 0) {
            buttonOffsetX = (Random.nextFloat() - 0.5f) * 120f
            buttonOffsetY = (Random.nextFloat() - 0.5f) * 120f
        }

        if (combo % 15 == 0) {
            starsEarned++
            soundManager.playCoin()
        }

        onScoreUpdate(score, combo, starsEarned)
    }

    LaunchedEffect(isTappedAnim) {
        if (isTappedAnim) {
            delay(60)
            isTappedAnim = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⏱️ ${timeLeftSeconds}s  •  Tap Frenzy!",
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = if (timeLeftSeconds <= 3) ArcadePink else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Total Taps: $tapsCount",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = ArcadePink
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = buttonOffsetX.dp, y = buttonOffsetY.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(ArcadePink)
                .clickable { handleRapidTap() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚡", fontSize = 54.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "TAP FAST!",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Tapping Speed: ${"%.1f".format(tapsCount / (16 - timeLeftSeconds).coerceAtLeast(1).toFloat())} taps/sec",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
