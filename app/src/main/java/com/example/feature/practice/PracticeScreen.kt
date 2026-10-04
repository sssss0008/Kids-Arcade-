package com.example.feature.practice

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.domain.models.*
import com.example.ui.theme.*

data class PracticeSkill(
    val title: String,
    val description: String,
    val emoji: String,
    val targetGameId: String,
    val color: Color
)

@Composable
fun PracticeScreen(
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager,
    onLaunchGame: (Game) -> Unit,
    onEarnRewards: (xp: Int, stars: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Skills, 1: Arcade Brain Quiz

    val skills = listOf(
        PracticeSkill("Reflex Lab", "Train your split-second hand-eye reaction speed", "⚡", "reflex_master", ArcadePink),
        PracticeSkill("Focus & Memory", "Strengthen visual memory & recall matching symbols", "🧠", "memory_cards", ArcadePurple),
        PracticeSkill("Speed Tapping", "Boost finger agility with rapid speed challenges", "⏱️", "fast_fingers", ArcadeCyan),
        PracticeSkill("Color Perception", "Sharpen quick chroma pattern recognition", "🎨", "color_tap", ArcadeOrange),
        PracticeSkill("Path Navigation", "Improve spatial reasoning and mini-maze navigation", "🌀", "maze_run", ArcadeIndigoPrimary),
        PracticeSkill("Coordination Hopper", "Enhance timing and rhythmic leap precision", "🐸", "jumping_frog", ArcadeGreen)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("practice_screen_scroll"),
        contentPadding = PaddingValues(16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "Arcade Academy 🎓",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Practice core skills, reflexes, memory, and train your brain!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tab Selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(18.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; soundManager.playClick() },
                    text = { Text("Skill Training 🏋️", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; soundManager.playClick() },
                    text = { Text("Brain Quiz 💡", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            items(skills) { skill ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            val game = ArcadeCatalog.games.find { it.id == skill.targetGameId } ?: ArcadeCatalog.games.first()
                            onLaunchGame(game)
                        }
                        .testTag("practice_skill_${skill.targetGameId}"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(skill.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(skill.emoji, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = skill.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = skill.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = {
                                val game = ArcadeCatalog.games.find { it.id == skill.targetGameId } ?: ArcadeCatalog.games.first()
                                onLaunchGame(game)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = skill.color)
                        ) {
                            Text("TRAIN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // Brain Quiz Section
            item {
                InteractiveBrainQuiz(
                    soundManager = soundManager,
                    hapticManager = hapticManager,
                    onReward = onEarnRewards
                )
            }
        }
    }
}

@Composable
fun InteractiveBrainQuiz(
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager,
    onReward: (xp: Int, stars: Int) -> Unit
) {
    var questionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }

    val questions = ArcadeCatalog.quizQuestions
    val currentQ = questions[questionIndex]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ArcadeIndigoPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = currentQ.category,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = ArcadeIndigoPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "Q ${questionIndex + 1}/${questions.size}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(currentQ.emoji, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentQ.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Options
            currentQ.options.forEachIndexed { idx, opt ->
                val isSelected = selectedOptionIndex == idx
                val isCorrect = idx == currentQ.correctIndex

                val btnColor = when {
                    !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                    isCorrect -> ArcadeGreen.copy(alpha = 0.25f)
                    isSelected -> ArcadePink.copy(alpha = 0.25f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isAnswered) {
                            selectedOptionIndex = idx
                            isAnswered = true
                            if (isCorrect) {
                                soundManager.playSuccess()
                                hapticManager.success()
                                correctCount++
                                onReward(60, 2)
                            } else {
                                soundManager.playFailure()
                                hapticManager.failure()
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = btnColor
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = listOf("A", "B", "C", "D")[idx],
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (isAnswered) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ArcadeIndigoPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 ${currentQ.explanation}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = ArcadeIndigoPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (questionIndex < questions.size - 1) {
                            questionIndex++
                            selectedOptionIndex = null
                            isAnswered = false
                        } else {
                            // Quiz finished
                            questionIndex = 0
                            selectedOptionIndex = null
                            isAnswered = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (questionIndex < questions.size - 1) "Next Question →" else "Play Quiz Again 🔄",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
