package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey val gameId: String,
    val highScore: Int = 0,
    val totalStars: Int = 0,
    val timesPlayed: Int = 0,
    val bestCombo: Int = 0,
    val bestDurationSeconds: Long = 0,
    val lastPlayedTimestamp: Long = 0
)

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Arcade Champ",
    val avatarEmoji: String = "🦊",
    val xp: Int = 250,
    val totalStars: Int = 42,
    val coins: Int = 200,
    val currentStreak: Int = 3,
    val gamesPlayedCount: Int = 6,
    val isParentalLockActive: Boolean = false,
    val lastActiveDate: String = ""
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val xpReward: Int,
    val starsReward: Int,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1
)

@Entity(tableName = "favorite_games")
data class FavoriteGameEntity(
    @PrimaryKey val gameId: String
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val soundEffectsEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val masterVolume: Float = 0.8f,
    val hapticsEnabled: Boolean = true,
    val defaultDifficulty: String = "NORMAL",
    val highContrastEnabled: Boolean = false,
    val reducedMotionEnabled: Boolean = false,
    val playTimeReminderMinutes: Int = 20
)
