package com.example.feature.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

data class LaneItem(
    val id: Long,
    val lane: Int, // 0, 1, 2
    var y: Float, // 0..1
    val isCoin: Boolean,
    val isStar: Boolean = false,
    val isCone: Boolean = false
)

@Composable
fun CoinHunterGame(
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
    var coinsCollected by remember { mutableIntStateOf(0) }

    var currentLane by remember { mutableIntStateOf(1) } // 0: Left, 1: Middle, 2: Right
    val items = remember { mutableStateListOf<LaneItem>() }
    var nextId by remember { mutableLongStateOf(0L) }
    var gameSpeed by remember { mutableFloatStateOf(0.009f) }

    // Motion & Spawning Loop
    LaunchedEffect(isPaused) {
        var frame = 0
        while (!isPaused) {
            delay(16)
            frame++

            // Accelerate slightly over time
            if (frame % 300 == 0) {
                gameSpeed = (gameSpeed + 0.001f).coerceAtMost(0.022f)
            }

            // Spawn items
            if (items.size < 6 && Random.nextFloat() < 0.08f) {
                val lane = Random.nextInt(3)
                val rand = Random.nextFloat()
                val isCone = rand < 0.28f
                val isStar = !isCone && rand < 0.40f

                items.add(
                    LaneItem(
                        id = nextId++,
                        lane = lane,
                        y = -0.1f,
                        isCoin = !isCone && !isStar,
                        isStar = isStar,
                        isCone = isCone
                    )
                )
            }

            // Move items down
            val iter = items.listIterator()
            while (iter.hasNext()) {
                val item = iter.next()
                item.y += gameSpeed

                // Collision with player (player is at y = 0.82)
                if (item.y in 0.77f..0.87f && item.lane == currentLane) {
                    iter.remove()
                    if (item.isCone) {
                        soundManager.playFailure()
                        hapticManager.failure()
                        onGameOver(score, maxCombo, starsEarned)
                        return@LaunchedEffect
                    } else if (item.isStar) {
                        starsEarned += 2
                        score += 150
                        soundManager.playSuccess()
                    } else {
                        // Coin
                        coinsCollected++
                        combo++
                        if (combo > maxCombo) maxCombo = combo
                        score += 35 * (1 + combo / 4)
                        soundManager.playCoin()
                        hapticManager.pop()
                    }
                    onScoreUpdate(score, combo, starsEarned)
                    continue
                }

                if (item.y > 1.1f) {
                    iter.remove()
                    if (item.isCoin) {
                        combo = 0
                        onScoreUpdate(score, combo, starsEarned)
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Track Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val laneWidth = size.width / 3f

                // Lane divider lines
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = Offset(laneWidth, 0f),
                    end = Offset(laneWidth, size.height),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = Offset(laneWidth * 2f, 0f),
                    end = Offset(laneWidth * 2f, size.height),
                    strokeWidth = 4f
                )

                // Items in lanes
                items.forEach { item ->
                    val ix = (item.lane + 0.5f) * laneWidth
                    val iy = item.y * size.height

                    if (item.isCone) {
                        // Traffic Cone
                        drawCircle(
                            color = ArcadeOrange,
                            radius = 22f,
                            center = Offset(ix, iy)
                        )
                    } else if (item.isStar) {
                        drawCircle(
                            color = ArcadeGold,
                            radius = 20f,
                            center = Offset(ix, iy)
                        )
                    } else {
                        // Shiny Coin
                        drawCircle(
                            color = ArcadeGold,
                            radius = 18f,
                            center = Offset(ix, iy)
                        )
                    }
                }

                // Player Kart in currentLane
                val px = (currentLane + 0.5f) * laneWidth
                val py = size.height * 0.82f
                drawCircle(
                    color = ArcadeCyan,
                    radius = 28f,
                    center = Offset(px, py)
                )
            }

            // Emojis for lane items on canvas
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(
                            x = (item.lane * 115 + 40).dp,
                            y = (item.y * 520).dp
                        )
                ) {
                    Text(
                        text = if (item.isCone) "🚧" else if (item.isStar) "⭐" else "🪙",
                        fontSize = 24.sp
                    )
                }
            }

            // Player emoji
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(
                        x = (currentLane * 115 + 40).dp,
                        y = (0.80f * 520).dp
                    )
            ) {
                Text("🏎️", fontSize = 32.sp)
            }
        }

        // Steer Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (currentLane > 0) {
                        currentLane--
                        hapticManager.tap()
                    }
                },
                modifier = Modifier
                    .size(width = 130.dp, height = 56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcadeIndigoPrimary)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left")
                Spacer(modifier = Modifier.width(6.dp))
                Text("LEFT", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (currentLane < 2) {
                        currentLane++
                        hapticManager.tap()
                    }
                },
                modifier = Modifier
                    .size(width = 130.dp, height = 56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcadeIndigoPrimary)
            ) {
                Text("RIGHT", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right")
            }
        }
    }
}
