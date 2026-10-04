package com.example.feature.gameloop

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.audio.ArcadeSoundManager
import com.example.core.haptics.ArcadeHapticManager
import com.example.core.ui.UniversalCountdownOverlay
import com.example.core.ui.UniversalGameHUD
import com.example.core.ui.UniversalGameResultScreen
import com.example.core.ui.UniversalPauseDialog
import com.example.domain.models.*
import com.example.feature.minigames.*
import kotlinx.coroutines.delay

enum class GameState {
    COUNTDOWN,
    PLAYING,
    PAUSED,
    RESULT
}

@Composable
fun UniversalGamePlayer(
    game: Game,
    soundManager: ArcadeSoundManager,
    hapticManager: ArcadeHapticManager,
    bestScore: Int,
    onSaveResult: suspend (GameResult) -> Boolean,
    onExitGame: () -> Unit,
    onNextGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var gameState by remember { mutableStateOf(GameState.COUNTDOWN) }
    var countdownValue by remember { mutableIntStateOf(3) }

    var currentScore by remember { mutableIntStateOf(0) }
    var currentCombo by remember { mutableIntStateOf(0) }
    var currentStars by remember { mutableIntStateOf(0) }
    var gameStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var gameResult by remember { mutableStateOf<GameResult?>(null) }
    var gameSessionKey by remember { mutableIntStateOf(0) } // increment to restart

    // Handle back button
    BackHandler {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED
        } else if (gameState == GameState.PAUSED || gameState == GameState.RESULT) {
            onExitGame()
        }
    }

    // Countdown effect
    LaunchedEffect(gameState, gameSessionKey) {
        if (gameState == GameState.COUNTDOWN) {
            countdownValue = 3
            soundManager.playCountDownTick()
            delay(800)
            countdownValue = 2
            soundManager.playCountDownTick()
            delay(800)
            countdownValue = 1
            soundManager.playCountDownTick()
            delay(800)
            countdownValue = 0
            soundManager.playCountDownGo()
            delay(400)
            gameStartTime = System.currentTimeMillis()
            gameState = GameState.PLAYING
        }
    }

    fun handleGameOver(finalScore: Int, maxCombo: Int, starsEarned: Int) {
        val durationSeconds = ((System.currentTimeMillis() - gameStartTime) / 1000).coerceAtLeast(1)
        val xpEarned = (finalScore / 2).coerceAtLeast(20) + (starsEarned * 10)
        val isNewRecord = finalScore > bestScore

        val result = GameResult(
            gameId = game.id,
            gameTitle = game.title,
            score = finalScore,
            bestScore = maxOf(bestScore, finalScore),
            xpEarned = xpEarned,
            starsEarned = starsEarned,
            combo = maxCombo,
            accuracy = 0.95f,
            duration = durationSeconds,
            isNewRecord = isNewRecord
        )
        gameResult = result
        gameState = GameState.RESULT

        // Trigger coroutine save via LaunchedEffect
    }

    LaunchedEffect(gameResult) {
        gameResult?.let { result ->
            onSaveResult(result)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Active Game Component based on gameId
        key(gameSessionKey) {
            val isGamePaused = (gameState != GameState.PLAYING)

            Box(modifier = Modifier.fillMaxSize().padding(top = 70.dp)) {
                when (game.id) {
                    "balloon_pop" -> BalloonPopGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "bubble_burst" -> BubbleBurstGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "catch_fish" -> CatchFishGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "jumping_frog" -> JumpingFrogGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "flying_bird" -> FlyingBirdGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "monkey_swing" -> MonkeySwingGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "candy_catcher", "fruit_catcher" -> CandyCatcherGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "treasure_collector", "toy_collector" -> TreasureCollectorGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "coin_hunter", "animal_runner" -> CoinHunterGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "star_catcher" -> StarCatcherGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "color_tap" -> ColorTapGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "reflex_master" -> ReflexMasterGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "memory_cards" -> MemoryCardsGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "fast_fingers" -> FastFingersGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "maze_run" -> MazeRunGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    "racing_fun" -> RacingFunGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                    else -> BalloonPopGame(
                        isPaused = isGamePaused,
                        soundManager = soundManager,
                        hapticManager = hapticManager,
                        onScoreUpdate = { s, c, st -> currentScore = s; currentCombo = c; currentStars = st },
                        onGameOver = { s, c, st -> handleGameOver(s, c, st) }
                    )
                }
            }
        }

        // Always show HUD when playing or paused
        if (gameState == GameState.PLAYING || gameState == GameState.PAUSED) {
            UniversalGameHUD(
                title = game.title,
                score = currentScore,
                combo = currentCombo,
                stars = currentStars,
                onPauseClick = {
                    gameState = GameState.PAUSED
                    soundManager.playClick()
                }
            )
        }

        // Countdown State Overlay
        if (gameState == GameState.COUNTDOWN) {
            UniversalCountdownOverlay(count = countdownValue)
        }

        // Pause Menu Dialog
        if (gameState == GameState.PAUSED) {
            UniversalPauseDialog(
                onResume = {
                    gameState = GameState.PLAYING
                    soundManager.playClick()
                },
                onRestart = {
                    currentScore = 0
                    currentCombo = 0
                    currentStars = 0
                    gameSessionKey++
                    gameState = GameState.COUNTDOWN
                    soundManager.playClick()
                },
                onExit = onExitGame,
                isSoundOn = soundManager.isSoundEnabled,
                onToggleSound = {
                    soundManager.isSoundEnabled = !soundManager.isSoundEnabled
                }
            )
        }

        // Game Over Result Screen
        if (gameState == GameState.RESULT && gameResult != null) {
            UniversalGameResultScreen(
                result = gameResult!!,
                onPlayAgain = {
                    currentScore = 0
                    currentCombo = 0
                    currentStars = 0
                    gameResult = null
                    gameSessionKey++
                    gameState = GameState.COUNTDOWN
                },
                onNextGame = onNextGame,
                onBackToGames = onExitGame,
                onHome = onExitGame
            )
        }
    }
}
