package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.data.local.ArcadeDatabase
import com.example.data.repository.ArcadeRepository
import com.example.domain.models.*
import com.example.feature.about.AboutScreen
import com.example.feature.drawer.*
import com.example.feature.gameloop.UniversalGamePlayer
import com.example.feature.games.GamesScreen
import com.example.feature.home.HomeScreen
import com.example.feature.onboarding.OnboardingDialog
import com.example.feature.practice.PracticeScreen
import com.example.ui.theme.KidsArcadeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: ArcadeSoundManager
    private lateinit var hapticManager: ArcadeHapticManager
    private lateinit var repository: ArcadeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundManager = ArcadeSoundManager()
        hapticManager = ArcadeHapticManager(this)

        val database = ArcadeDatabase.getInstance(this)
        repository = ArcadeRepository(database.arcadeDao())

        setContent {
            val settings by repository.settingsFlow.collectAsStateWithLifecycle(initialValue = UserSettings())

            // Sync settings with managers
            LaunchedEffect(settings) {
                soundManager.isSoundEnabled = settings.soundEffectsEnabled
                soundManager.isMusicEnabled = settings.musicEnabled
                soundManager.masterVolume = settings.masterVolume
                hapticManager.isHapticsEnabled = settings.hapticsEnabled
            }

            KidsArcadeTheme {
                ArcadeMainApp(
                    repository = repository,
                    soundManager = soundManager,
                    hapticManager = hapticManager
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcadeMainApp(
    repository: ArcadeRepository,
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val profile by repository.playerProfileFlow.collectAsStateWithLifecycle(initialValue = PlayerProfile())
    val gameRecords by repository.gameRecordsFlow.collectAsStateWithLifecycle(initialValue = emptyMap())
    val favoriteIds by repository.favoriteIdsFlow.collectAsStateWithLifecycle(initialValue = emptySet())
    val achievements by repository.achievementsFlow.collectAsStateWithLifecycle(initialValue = ArcadeCatalog.initialAchievements)
    val settings by repository.settingsFlow.collectAsStateWithLifecycle(initialValue = UserSettings())

    // Exactly 4 primary bottom navigation tabs
    var currentBottomTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Games, 2: Practice, 3: About
    var activeGame by remember { mutableStateOf<Game?>(null) }

    // Dialog flags for drawer destinations
    var showAchievementsDialog by remember { mutableStateOf(false) }
    var showMissionsDialog by remember { mutableStateOf(false) }
    var showWorldsDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showParentAreaDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showOnboardingDialog by remember { mutableStateOf(false) }

    // First time onboarding check
    var hasCheckedOnboarding by remember { mutableStateOf(false) }
    LaunchedEffect(profile) {
        if (!hasCheckedOnboarding) {
            hasCheckedOnboarding = true
            if (profile.gamesPlayedCount == 0 && profile.name == "Arcade Kid") {
                showOnboardingDialog = true
            }
        }
    }

    // Back handler for bottom tabs
    BackHandler(enabled = activeGame == null && currentBottomTab != 0) {
        currentBottomTab = 0
    }

    // Active Fullscreen Mini-Game Player
    if (activeGame != null) {
        UniversalGamePlayer(
            game = activeGame!!,
            soundManager = soundManager,
            hapticManager = hapticManager,
            bestScore = gameRecords[activeGame!!.id]?.highScore ?: 0,
            onSaveResult = { result ->
                repository.saveGameResult(result)
            },
            onExitGame = {
                activeGame = null
            },
            onNextGame = {
                val all = ArcadeCatalog.games
                val nextIdx = (all.indexOf(activeGame) + 1) % all.size
                activeGame = all[nextIdx]
            }
        )
        return
    }

    // Standard Scaffold with Navigation Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ArcadeDrawerContent(
                profile = profile,
                onNavigate = { route ->
                    when (route) {
                        "games" -> currentBottomTab = 1
                        "random_game" -> activeGame = ArcadeCatalog.games.random()
                        "missions" -> showMissionsDialog = true
                        "achievements" -> showAchievementsDialog = true
                        "stats" -> showStatsDialog = true
                        "worlds" -> showWorldsDialog = true
                        "collection" -> showWorldsDialog = true
                        "profile" -> showProfileDialog = true
                        "parent" -> showParentAreaDialog = true
                        "settings" -> showSettingsDialog = true
                        "about" -> currentBottomTab = 3
                    }
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (currentBottomTab) {
                                    0 -> "Kids Arcade"
                                    1 -> "All Games"
                                    2 -> "Practice Lab"
                                    else -> "About Us"
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                soundManager.playClick()
                                coroutineScope.launch { drawerState.open() }
                            },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // Quick streak indicator
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { showProfileDialog = true }
                                .padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(profile.avatarEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("⭐ ${profile.totalStars}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                // Exactly 4 primary items
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("arcade_bottom_nav")
                ) {
                    // TAB 1: Home
                    NavigationBarItem(
                        selected = currentBottomTab == 0,
                        onClick = {
                            currentBottomTab = 0
                            soundManager.playClick()
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    // TAB 2: Games
                    NavigationBarItem(
                        selected = currentBottomTab == 1,
                        onClick = {
                            currentBottomTab = 1
                            soundManager.playClick()
                        },
                        icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Games") },
                        label = { Text("Games", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_item_games")
                    )

                    // TAB 3: Practice
                    NavigationBarItem(
                        selected = currentBottomTab == 2,
                        onClick = {
                            currentBottomTab = 2
                            soundManager.playClick()
                        },
                        icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Practice") },
                        label = { Text("Practice", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_item_practice")
                    )

                    // TAB 4: About
                    NavigationBarItem(
                        selected = currentBottomTab == 3,
                        onClick = {
                            currentBottomTab = 3
                            soundManager.playClick()
                        },
                        icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                        label = { Text("About", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_item_about")
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentBottomTab) {
                    0 -> HomeScreen(
                        profile = profile,
                        gameRecords = gameRecords,
                        favoriteIds = favoriteIds,
                        onGameSelect = { game ->
                            soundManager.playClick()
                            activeGame = game
                        },
                        onRandomGameSelect = {
                            soundManager.playClick()
                            activeGame = ArcadeCatalog.games.random()
                        },
                        onFavoriteToggle = { id, isFav ->
                            coroutineScope.launch {
                                repository.toggleFavorite(id, isFav)
                            }
                        },
                        onProfileClick = { showProfileDialog = true }
                    )
                    1 -> GamesScreen(
                        gameRecords = gameRecords,
                        favoriteIds = favoriteIds,
                        onGameSelect = { game ->
                            soundManager.playClick()
                            activeGame = game
                        },
                        onFavoriteToggle = { id, isFav ->
                            coroutineScope.launch {
                                repository.toggleFavorite(id, isFav)
                            }
                        }
                    )
                    2 -> PracticeScreen(
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onLaunchGame = { game ->
                            soundManager.playClick()
                            activeGame = game
                        },
                        onEarnRewards = { xp, stars ->
                            coroutineScope.launch {
                                repository.saveGameResult(
                                    GameResult(
                                        gameId = "brain_quiz",
                                        gameTitle = "Brain Quiz",
                                        score = xp,
                                        bestScore = xp,
                                        xpEarned = xp,
                                        starsEarned = stars,
                                        combo = 1,
                                        accuracy = 1f,
                                        duration = 30,
                                        isNewRecord = false
                                    )
                                )
                            }
                        }
                    )
                    3 -> AboutScreen(
                        soundManager = soundManager
                    )
                }
            }
        }
    }

    // Modals & Dialogs
    if (showAchievementsDialog) {
        AchievementsModal(
            achievements = achievements,
            onDismiss = { showAchievementsDialog = false }
        )
    }

    if (showMissionsDialog) {
        MissionsAndChallengesDialog(
            onDismiss = { showMissionsDialog = false }
        )
    }

    if (showWorldsDialog) {
        ArcadeWorldsDialog(
            totalStars = profile.totalStars,
            onDismiss = { showWorldsDialog = false }
        )
    }

    if (showStatsDialog) {
        StatisticsDialog(
            gameRecords = gameRecords,
            profile = profile,
            onDismiss = { showStatsDialog = false }
        )
    }

    if (showProfileDialog) {
        PlayerProfileDialog(
            profile = profile,
            onSaveProfile = { name, avatar ->
                coroutineScope.launch {
                    repository.updateProfile(name, avatar)
                }
            },
            onDismiss = { showProfileDialog = false }
        )
    }

    if (showParentAreaDialog) {
        ParentAreaDialog(
            profile = profile,
            settings = settings,
            onUpdateSettings = { newSettings ->
                coroutineScope.launch {
                    repository.updateSettings(newSettings)
                }
            },
            onResetProgress = {
                coroutineScope.launch {
                    repository.resetProgress()
                }
            },
            onDismiss = { showParentAreaDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            settings = settings,
            soundManager = soundManager,
            hapticManager = hapticManager,
            onUpdateSettings = { newSettings ->
                coroutineScope.launch {
                    repository.updateSettings(newSettings)
                }
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showOnboardingDialog) {
        OnboardingDialog(
            onComplete = { name, avatar ->
                showOnboardingDialog = false
                coroutineScope.launch {
                    repository.updateProfile(name, avatar)
                }
            }
        )
    }
}
