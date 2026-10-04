package com.example.feature.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

data class TrackItem(
    val id: Long,
    val lane: Int,
    var y: Float,
    val isBoost: Boolean,
    val isHazard: Boolean,
    val isStar: Boolean
)

@Composable
fun RacingFunGame(
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
    var distanceMeters by remember { mutableIntStateOf(0) }

    var kartLane by remember { mutableIntStateOf(1) } // 0, 1, 2
    var trackSpeed by remember { mutableFloatStateOf(0.012f) }
    var isTurboActive by remember { mutableStateOf(false) }

    val trackItems = remember { mutableStateListOf<TrackItem>() }
    var nextId by remember { mutableLongStateOf(0L) }

    // Race physics loop
    LaunchedEffect(isPaused) {
        var frame = 0
        while (!isPaused) {
            delay(16)
            frame++

            distanceMeters += if (isTurboActive) 2 else 1

            // Spawn track items
            if (trackItems.size < 6 && Random.nextFloat() < 0.08f) {
                val lane = Random.nextInt(3)
                val rand = Random.nextFloat()
                val isBoost = rand < 0.22f
                val isHazard = !isBoost && rand < 0.50f
                val isStar = !isBoost && !isHazard

                trackItems.add(
                    TrackItem(
                        id = nextId++,
                        lane = lane,
                        y = -0.1f,
                        isBoost = isBoost,
                        isHazard = isHazard,
                        isStar = isStar
                    )
                )
            }

            // Move items down
            val currentSpeed = if (isTurboActive) trackSpeed * 1.8f else trackSpeed
            val iter = trackItems.listIterator()
            while (iter.hasNext()) {
                val item = iter.next()
                item.y += currentSpeed

                // Hit test (kart is at y = 0.82)
                if (item.y in 0.76f..0.88f && item.lane == kartLane) {
                    iter.remove()
                    if (item.isHazard) {
                        if (!isTurboActive) {
                            soundManager.playFailure()
                            hapticManager.failure()
                            onGameOver(score, maxCombo, starsEarned)
                            return@LaunchedEffect
                        }
                    } else if (item.isBoost) {
                        soundManager.playLevelUp()
                        hapticManager.combo()
                        isTurboActive = true
                        combo += 2
                        if (combo > maxCombo) maxCombo = combo
                        score += 150
                    } else if (item.isStar) {
                        starsEarned++
                        score += 80
                        soundManager.playCoin()
                    }
                    onScoreUpdate(score, combo, starsEarned)
                    continue
                }

                if (item.y > 1.1f) {
                    iter.remove()
                }
            }
        }
    }

    // Reset turbo after delay
    LaunchedEffect(isTurboActive) {
        if (isTurboActive) {
            delay(2200)
            isTurboActive = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val laneWidth = size.width / 3f

                // Track tarmac
                drawRect(color = Color(0xFF1E293B), size = size)

                // Road stripes
                drawLine(
                    color = ArcadeGold.copy(alpha = 0.6f),
                    start = Offset(laneWidth, 0f),
                    end = Offset(laneWidth, size.height),
                    strokeWidth = 6f
                )
                drawLine(
                    color = ArcadeGold.copy(alpha = 0.6f),
                    start = Offset(laneWidth * 2f, 0f),
                    end = Offset(laneWidth * 2f, size.height),
                    strokeWidth = 6f
                )
            }

            // Items
            trackItems.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(
                            x = (item.lane * 115 + 40).dp,
                            y = (item.y * 520).dp
                        )
                ) {
                    Text(
                        text = if (item.isBoost) "⚡" else if (item.isHazard) "🛢️" else "⭐",
                        fontSize = 28.sp
                    )
                }
            }

            // Kart
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(
                        x = (kartLane * 115 + 40).dp,
                        y = (0.80f * 520).dp
                    )
            ) {
                Text(if (isTurboActive) "🔥🏎️" else "🏎️", fontSize = 34.sp)
            }

            // HUD overlay on top of track
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🏁 ${distanceMeters}m", fontWeight = FontWeight.Black, fontSize = 18.sp, color = ArcadeGold)
                if (isTurboActive) {
                    Text("TURBO BOOST! ⚡", fontWeight = FontWeight.Black, fontSize = 16.sp, color = ArcadePink)
                }
            }
        }

        // Steer buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (kartLane > 0) {
                        kartLane--
                        hapticManager.tap()
                    }
                },
                modifier = Modifier.size(width = 130.dp, height = 56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcadeRose)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left")
                Spacer(modifier = Modifier.width(6.dp))
                Text("STEER", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (kartLane < 2) {
                        kartLane++
                        hapticManager.tap()
                    }
                },
                modifier = Modifier.size(width = 130.dp, height = 56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcadeRose)
            ) {
                Text("STEER", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right")
            }
        }
    }
}
