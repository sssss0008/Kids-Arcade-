package com.example.feature.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.models.ArcadeCatalog
import com.example.ui.theme.*

@Composable
fun OnboardingDialog(
    onComplete: (name: String, avatar: String) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var playerName by remember { mutableStateOf("Arcade Champion") }
    var selectedAvatar by remember { mutableStateOf("🦊") }

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (step) {
                    0 -> {
                        Text("🕹️", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Welcome to Kids Arcade Mania!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "A colorful arcade paradise loaded with awesome mini-games, fun challenges, and rewards!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    1 -> {
                        Text("🎈", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Play 20+ Mini-Games",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Pop balloons, leap with frogs, catch sweet treats, race fast karts, and explore temples!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    2 -> {
                        Text("⚡", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Train Your Reflexes & Memory",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Level up your hand-eye coordination, focus, memory matching, and reaction speed.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    3 -> {
                        Text("👑", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Become Ultimate Champion!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Earn XP, collect shining stars, complete daily missions, and climb from Beginner to Champion!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    4 -> {
                        // Setup profile
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(ArcadeIndigoPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selectedAvatar, fontSize = 40.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Choose Your Player Name", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = playerName,
                            onValueChange = { playerName = it },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Pick Your Mascot", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("🦊", "🐼", "🐸", "🦁", "🤖").forEach { av ->
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (selectedAvatar == av) ArcadeGold else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedAvatar = av },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(av, fontSize = 26.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (step < 4) {
                            step++
                        } else {
                            onComplete(playerName.trim().ifEmpty { "Arcade Champion" }, selectedAvatar)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArcadeIndigoPrimary)
                ) {
                    Text(
                        text = if (step < 4) "Continue →" else "🚀 START MY ADVENTURE!",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
