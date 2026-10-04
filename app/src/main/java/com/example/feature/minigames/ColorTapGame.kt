package com.example.feature.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import kotlin.random.Random

data class ColorOption(val name: String, val color: Color)

val ArcadeGameColors = listOf(
    ColorOption("RED", ArcadePink),
    ColorOption("BLUE", ArcadeCyan),
    ColorOption("GREEN", ArcadeGreen),
    ColorOption("YELLOW", ArcadeGold),
    ColorOption("PURPLE", ArcadePurple),
    ColorOption("ORANGE", ArcadeOrange)
)

@Composable
fun ColorTapGame(
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
    var roundsCompleted by remember { mutableIntStateOf(0) }
    var timeLeftSeconds by remember { mutableIntStateOf(30) }

    var targetColor by remember { mutableStateOf(ArcadeGameColors.random()) }
    var displayedOptions by remember { mutableStateOf(ArcadeGameColors.shuffled().take(4)) }

    fun nextRound() {
        val nextTarget = ArcadeGameColors.random()
        val otherThree = ArcadeGameColors.filter { it != nextTarget }.shuffled().take(3)
        targetColor = nextTarget
        displayedOptions = (otherThree + nextTarget).shuffled()
    }

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

    fun handleColorTap(option: ColorOption) {
        if (isPaused) return
        if (option == targetColor) {
            soundManager.playPop()
            hapticManager.pop()
            combo++
            if (combo > maxCombo) maxCombo = combo
            score += 50 * (1 + combo / 3)
            roundsCompleted++

            if (combo % 5 == 0) {
                starsEarned++
                soundManager.playCoin()
            }
        } else {
            soundManager.playFailure()
            hapticManager.failure()
            combo = 0
            score = (score - 40).coerceAtLeast(0)
        }
        onScoreUpdate(score, combo, starsEarned)
        nextRound()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⏱️ ${timeLeftSeconds}s",
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = if (timeLeftSeconds <= 5) ArcadePink else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Target Prompt
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TAP THIS COLOR:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = targetColor.name,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = targetColor.color
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2x2 Color Grid
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorTile(displayedOptions[0], modifier = Modifier.weight(1f)) { handleColorTap(displayedOptions[0]) }
                ColorTile(displayedOptions[1], modifier = Modifier.weight(1f)) { handleColorTap(displayedOptions[1]) }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorTile(displayedOptions[2], modifier = Modifier.weight(1f)) { handleColorTap(displayedOptions[2]) }
                ColorTile(displayedOptions[3], modifier = Modifier.weight(1f)) { handleColorTap(displayedOptions[3]) }
            }
        }
    }
}

@Composable
fun ColorTile(option: ColorOption, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(option.color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = option.name,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
    }
}
