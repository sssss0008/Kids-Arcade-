package com.example.feature.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.domain.models.*
import com.example.feature.about.ParentGateDialog
import com.example.ui.theme.*

/**
 * Animated Navigation Drawer Content
 */
@Composable
fun ArcadeDrawerContent(
    profile: PlayerProfile,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Drawer Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArcadeIndigoPrimary)
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(profile.avatarEmoji, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = profile.name,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${profile.rank.badgeIcon} ${profile.rank.title}",
                                fontWeight = FontWeight.Bold,
                                color = ArcadeGold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("⚡ ${profile.xp} XP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("⭐ ${profile.totalStars} Stars", color = ArcadeGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("🔥 ${profile.currentStreak}d Streak", color = ArcadeOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PLAY SECTION
            DrawerSectionHeader("PLAY")
            DrawerMenuItem("All Games", "🎮") { onNavigate("games"); onCloseDrawer() }
            DrawerMenuItem("Random Game", "🎲") { onNavigate("random_game"); onCloseDrawer() }

            // CHALLENGES SECTION
            DrawerSectionHeader("CHALLENGES")
            DrawerMenuItem("Daily & Weekly Missions", "🎯") { onNavigate("missions"); onCloseDrawer() }

            // PROGRESS SECTION
            DrawerSectionHeader("PROGRESS")
            DrawerMenuItem("Achievements", "🏆") { onNavigate("achievements"); onCloseDrawer() }
            DrawerMenuItem("Game Statistics & Records", "📊") { onNavigate("stats"); onCloseDrawer() }

            // COLLECTION SECTION
            DrawerSectionHeader("COLLECTION")
            DrawerMenuItem("Arcade Worlds", "🗺️") { onNavigate("worlds"); onCloseDrawer() }
            DrawerMenuItem("Items & Medals Collection", "💎") { onNavigate("collection"); onCloseDrawer() }

            // MORE
            DrawerSectionHeader("MORE")
            DrawerMenuItem("Player Profile & Avatars", "👤") { onNavigate("profile"); onCloseDrawer() }
            DrawerMenuItem("Parent Area 🔒", "👨‍👩‍👧") { onNavigate("parent"); onCloseDrawer() }
            DrawerMenuItem("Settings", "⚙️") { onNavigate("settings"); onCloseDrawer() }
            DrawerMenuItem("About Us", "ℹ️") { onNavigate("about"); onCloseDrawer() }
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun DrawerMenuItem(label: String, emoji: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Text(emoji, fontSize = 20.sp) },
        label = { Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Achievements Modal Dialog
 */
@Composable
fun AchievementsModal(
    achievements: List<Achievement>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Achievements 🏆", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "${achievements.count { it.isUnlocked }} / ${achievements.size} Unlocked",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(achievements) { a ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (a.isUnlocked) ArcadeGold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(a.iconEmoji, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(a.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(a.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("⚡ +${a.xpReward} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ArcadeIndigoPrimary)
                                        Text("⭐ +${a.starsReward} Stars", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ArcadeGoldDark)
                                    }
                                }
                                if (a.isUnlocked) {
                                    Text("✅", fontSize = 20.sp)
                                } else {
                                    Text("🔒", fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Missions & Challenges Dialog
 */
@Composable
fun MissionsAndChallengesDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Missions 🎯", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ArcadeCatalog.dailyMissions) { m ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(m.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("⚡ +${m.xpReward} XP", color = ArcadeIndigoPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text(m.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                val progress = (m.currentCount.toFloat() / m.targetCount.toFloat()).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = ArcadeGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${m.currentCount} / ${m.targetCount}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Arcade Worlds Dialog
 */
@Composable
fun ArcadeWorldsDialog(
    totalStars: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Arcade Worlds 🗺️", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Text("Your Stars: ⭐ $totalStars", fontWeight = FontWeight.Bold, color = ArcadeGoldDark)
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ArcadeCatalog.worlds) { w ->
                        val isUnlocked = totalStars >= w.requiredStars
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isUnlocked) w.themeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(w.emoji, fontSize = 34.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(w.name, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text(w.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isUnlocked) "UNLOCKED • ${w.gamesCount} Games" else "Requires ⭐ ${w.requiredStars} Stars",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnlocked) ArcadeGreen else ArcadePink
                                    )
                                }
                                Text(if (isUnlocked) "🔓" else "🔒", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Statistics Dialog
 */
@Composable
fun StatisticsDialog(
    gameRecords: Map<String, GameRecord>,
    profile: PlayerProfile,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Arcade Records 📊", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ArcadeCatalog.games) { game ->
                        val record = gameRecords[game.id]
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(game.iconEmoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(game.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Played: ${record?.timesPlayed ?: 0} times", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("🏆 ${record?.highScore ?: 0}", fontWeight = FontWeight.Black, color = ArcadeIndigoPrimary, fontSize = 15.sp)
                                    Text("⭐ ${record?.totalStars ?: 0}", fontSize = 12.sp, color = ArcadeGoldDark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Player Profile & Avatar Picker Dialog
 */
@Composable
fun PlayerProfileDialog(
    profile: PlayerProfile,
    onSaveProfile: (name: String, avatarEmoji: String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameInput by remember { mutableStateOf(profile.name) }
    var selectedAvatar by remember { mutableStateOf(profile.avatarEmoji) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
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
                    Text("Player Profile 👤", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                // Current Avatar Preview
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(ArcadeIndigoPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(selectedAvatar, fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Player Name") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("Choose Your Avatar", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ArcadeCatalog.avatars.chunked(4)) { rowAvatars ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowAvatars.forEach { a ->
                                val isSelected = selectedAvatar == a.emoji
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) ArcadeGold else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedAvatar = a.emoji },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(a.emoji, fontSize = 30.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onSaveProfile(nameInput.trim().ifEmpty { "Arcade Champ" }, selectedAvatar)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Parent Area with Adult Gate verification
 */
@Composable
fun ParentAreaDialog(
    profile: PlayerProfile,
    settings: UserSettings,
    onUpdateSettings: (UserSettings) -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    var isVerified by remember { mutableStateOf(false) }

    if (!isVerified) {
        ParentGateDialog(
            onSuccess = { isVerified = true },
            onDismiss = onDismiss
        )
    } else {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Parent Controls 👨‍👩‍👧", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Activity & Usage", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Total Games Played: ${profile.gamesPlayedCount}")
                    Text("• Total Stars Collected: ${profile.totalStars}")
                    Text("• Current Arcade Rank: ${profile.rank.title}")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Screen-Time Reminder", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Reminder Every: ${settings.playTimeReminderMinutes} min")
                        Slider(
                            value = settings.playTimeReminderMinutes.toFloat(),
                            onValueChange = { onUpdateSettings(settings.copy(playTimeReminderMinutes = it.toInt())) },
                            valueRange = 10f..60f,
                            steps = 4,
                            modifier = Modifier.width(140.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Data Management", fontWeight = FontWeight.Bold, color = ArcadePink)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            onResetProgress()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ArcadePink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset Game Progress")
                    }
                }
            }
        }
    }
}

/**
 * Settings Dialog
 */
@Composable
fun SettingsDialog(
    settings: UserSettings,
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager,
    onUpdateSettings: (UserSettings) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Settings ⚙️", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Audio & Sound", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects")
                    Switch(
                        checked = settings.soundEffectsEnabled,
                        onCheckedChange = {
                            val newS = settings.copy(soundEffectsEnabled = it)
                            soundManager.isSoundEnabled = it
                            onUpdateSettings(newS)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Haptics & Vibration", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Feedback")
                    Switch(
                        checked = settings.hapticsEnabled,
                        onCheckedChange = {
                            val newS = settings.copy(hapticsEnabled = it)
                            hapticManager.isHapticsEnabled = it
                            onUpdateSettings(newS)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Accessibility", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reduced Motion")
                    Switch(
                        checked = settings.reducedMotionEnabled,
                        onCheckedChange = {
                            onUpdateSettings(settings.copy(reducedMotionEnabled = it))
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("High Contrast")
                    Switch(
                        checked = settings.highContrastEnabled,
                        onCheckedChange = {
                            onUpdateSettings(settings.copy(highContrastEnabled = it))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
