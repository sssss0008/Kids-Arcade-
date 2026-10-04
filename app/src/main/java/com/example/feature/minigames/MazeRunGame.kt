package com.example.feature.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MazeRunGame(
    isPaused: Boolean,
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager,
    onScoreUpdate: (score: Int, combo: Int, stars: Int) -> Unit,
    onGameOver: (finalScore: Int, maxCombo: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableIntStateOf(0) }
    var starsEarned by remember { mutableIntStateOf(0) }
    var timeLeftSeconds by remember { mutableIntStateOf(45) }

    // 6x6 Maze Layout: 1 = wall, 0 = path, 2 = star, 3 = trophy
    val maze = remember {
        mutableStateListOf(
            mutableStateListOf(0, 0, 1, 0, 2, 0),
            mutableStateListOf(1, 0, 1, 0, 1, 0),
            mutableStateListOf(0, 0, 0, 0, 1, 2),
            mutableStateListOf(0, 1, 1, 0, 0, 0),
            mutableStateListOf(2, 0, 1, 1, 1, 0),
            mutableStateListOf(1, 0, 0, 0, 0, 3)
        )
    }

    var playerR by remember { mutableIntStateOf(0) }
    var playerC by remember { mutableIntStateOf(0) }

    LaunchedEffect(isPaused) {
        while (!isPaused && timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        }
        if (timeLeftSeconds <= 0) {
            soundManager.playFailure()
            onGameOver(score, 1, starsEarned)
        }
    }

    fun tryMove(dr: Int, dc: Int) {
        if (isPaused) return
        val nr = playerR + dr
        val nc = playerC + dc

        if (nr in 0 until 6 && nc in 0 until 6) {
            val cell = maze[nr][nc]
            if (cell != 1) { // Not wall
                playerR = nr
                playerC = nc
                hapticManager.tap()

                if (cell == 2) {
                    // Star
                    maze[nr][nc] = 0
                    starsEarned++
                    score += 100
                    soundManager.playCoin()
                } else if (cell == 3) {
                    // Trophy / Exit!
                    score += 300 + (timeLeftSeconds * 10)
                    soundManager.playSuccess()
                    hapticManager.success()
                    onScoreUpdate(score, 1, starsEarned)
                    onGameOver(score, 1, starsEarned)
                    return
                } else {
                    score += 10
                    soundManager.playPop()
                }
                onScoreUpdate(score, 1, starsEarned)
            } else {
                // Hit wall
                soundManager.playFailure()
                hapticManager.failure()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⏱️ ${timeLeftSeconds}s", fontWeight = FontWeight.Black, fontSize = 18.sp, color = ArcadePink)
            Text("⭐ Stars: $starsEarned / 3", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ArcadeGoldDark)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Maze Grid
        Box(
            modifier = Modifier
                .size(310.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ArcadeDarkSurface)
                .border(3.dp, ArcadeIndigoPrimary, RoundedCornerShape(20.dp))
                .padding(6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (r in 0 until 6) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (c in 0 until 6) {
                            val isPlayer = (r == playerR && c == playerC)
                            val cell = maze[r][c]

                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isPlayer -> ArcadeCyan
                                            cell == 1 -> ArcadeIndigoDark // Wall
                                            else -> Color.White.copy(alpha = 0.08f) // Path
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPlayer) {
                                    Text("🦊", fontSize = 24.sp)
                                } else {
                                    when (cell) {
                                        1 -> Text("🧱", fontSize = 16.sp)
                                        2 -> Text("⭐", fontSize = 18.sp)
                                        3 -> Text("🏆", fontSize = 22.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Direction Pad Controls
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = { tryMove(-1, 0) },
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ArcadeIndigoPrimary)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                IconButton(
                    onClick = { tryMove(0, -1) },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ArcadeIndigoPrimary)
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = Color.White)
                }

                IconButton(
                    onClick = { tryMove(1, 0) },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ArcadeIndigoPrimary)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White)
                }

                IconButton(
                    onClick = { tryMove(0, 1) },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ArcadeIndigoPrimary)
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = Color.White)
                }
            }
        }
    }
}
