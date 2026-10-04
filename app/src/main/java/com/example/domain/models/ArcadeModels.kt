package com.example.domain.models

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class PlayerRank(
    val title: String,
    val minXp: Int,
    val maxXp: Int,
    val color: Color,
    val badgeIcon: String
) {
    BEGINNER("Beginner", 0, 500, RankBeginnerColor, "🌱"),
    PLAYER("Player", 500, 1500, RankPlayerColor, "🎮"),
    PRO_GAMER("Pro Gamer", 1500, 3500, RankProColor, "⚡"),
    ARCADE_HERO("Arcade Hero", 3500, 7500, RankHeroColor, "🔥"),
    ULTIMATE_CHAMPION("Ultimate Champion", 7500, 15000, RankChampionColor, "👑");

    companion object {
        fun fromXp(xp: Int): PlayerRank {
            return when {
                xp >= ULTIMATE_CHAMPION.minXp -> ULTIMATE_CHAMPION
                xp >= ARCADE_HERO.minXp -> ARCADE_HERO
                xp >= PRO_GAMER.minXp -> PRO_GAMER
                xp >= PLAYER.minXp -> PLAYER
                else -> BEGINNER
            }
        }
    }
}

enum class GameCategory(val displayName: String, val iconEmoji: String, val themeColor: Color) {
    ALL("All", "⭐", ArcadeIndigoPrimary),
    REFLEX("Reflex", "⚡", ArcadePink),
    MEMORY("Memory", "🧠", ArcadePurple),
    SPEED("Speed", "🚀", ArcadeCyan),
    FOCUS("Focus", "🎯", ArcadeGold),
    COORDINATION("Coordination", "🎪", ArcadeGreen),
    ADVENTURE("Adventure", "🗺️", ArcadeAmber),
    RACING("Racing", "🏎️", ArcadeRose),
    PUZZLE("Puzzle", "🧩", ArcadeTeal)
}

enum class GameDifficulty(val label: String, val color: Color, val multiplier: Float) {
    EASY("Easy", ArcadeGreen, 1.0f),
    NORMAL("Normal", ArcadeCyan, 1.25f),
    ADVANCED("Advanced", ArcadeAmber, 1.5f),
    CHALLENGE("Challenge", ArcadePink, 2.0f)
}

data class Game(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: GameCategory,
    val difficulty: GameDifficulty,
    val iconEmoji: String,
    val accentColor: Color,
    val description: String,
    val rules: String,
    val isNew: Boolean = false,
    val isPopular: Boolean = false,
    val isFavorite: Boolean = false
)

data class GameResult(
    val gameId: String,
    val gameTitle: String,
    val score: Int,
    val bestScore: Int,
    val xpEarned: Int,
    val starsEarned: Int,
    val combo: Int,
    val accuracy: Float?,
    val duration: Long,
    val isNewRecord: Boolean
)

data class GameRecord(
    val gameId: String,
    val highScore: Int,
    val totalStars: Int,
    val timesPlayed: Int,
    val bestCombo: Int,
    val lastPlayedTimestamp: Long
)

data class PlayerProfile(
    val name: String = "Arcade Kid",
    val avatarEmoji: String = "🦊",
    val rank: PlayerRank = PlayerRank.BEGINNER,
    val xp: Int = 120,
    val totalStars: Int = 35,
    val coins: Int = 150,
    val currentStreak: Int = 2,
    val gamesPlayedCount: Int = 5,
    val isParentalLockActive: Boolean = false
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val xpReward: Int,
    val starsReward: Int,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1
)

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val targetGameId: String,
    val currentCount: Int,
    val targetCount: Int,
    val xpReward: Int,
    val starsReward: Int,
    val isCompleted: Boolean = false
)

data class WeeklyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val currentCount: Int,
    val targetCount: Int,
    val xpReward: Int,
    val starsReward: Int,
    val isCompleted: Boolean = false
)

data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val currentCount: Int,
    val targetCount: Int,
    val xpReward: Int,
    val starsReward: Int,
    val isCompleted: Boolean = false
)

data class ArcadeWorld(
    val id: String,
    val name: String,
    val emoji: String,
    val themeColor: Color,
    val description: String,
    val requiredStars: Int,
    val isUnlocked: Boolean,
    val gamesCount: Int
)

data class AvatarItem(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String,
    val isUnlocked: Boolean,
    val unlockNote: String
)

data class QuizQuestion(
    val id: String,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val emoji: String
)

data class UserSettings(
    val soundEffectsEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val masterVolume: Float = 0.8f,
    val hapticsEnabled: Boolean = true,
    val defaultDifficulty: GameDifficulty = GameDifficulty.NORMAL,
    val highContrastEnabled: Boolean = false,
    val reducedMotionEnabled: Boolean = false,
    val playTimeReminderMinutes: Int = 20, // 0 means off
    val sessionElapsedMinutes: Int = 0
)
