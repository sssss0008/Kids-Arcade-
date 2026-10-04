package com.example.data.repository

import com.example.data.local.ArcadeDao
import com.example.data.local.entities.*
import com.example.domain.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArcadeRepository(private val dao: ArcadeDao) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            // Seed initial achievements if empty
            val profile = dao.getPlayerProfile()
            if (profile == null) {
                dao.savePlayerProfile(PlayerProfileEntity())
                val initialEntities = ArcadeCatalog.initialAchievements.map {
                    AchievementEntity(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        iconEmoji = it.iconEmoji,
                        xpReward = it.xpReward,
                        starsReward = it.starsReward,
                        isUnlocked = it.isUnlocked,
                        progress = it.progress,
                        maxProgress = it.maxProgress
                    )
                }
                dao.saveAchievements(initialEntities)
            }
        }
    }

    val playerProfileFlow: Flow<PlayerProfile> = dao.getPlayerProfileFlow().map { entity ->
        if (entity == null) {
            PlayerProfile()
        } else {
            PlayerProfile(
                name = entity.name,
                avatarEmoji = entity.avatarEmoji,
                rank = PlayerRank.fromXp(entity.xp),
                xp = entity.xp,
                totalStars = entity.totalStars,
                coins = entity.coins,
                currentStreak = entity.currentStreak,
                gamesPlayedCount = entity.gamesPlayedCount,
                isParentalLockActive = entity.isParentalLockActive
            )
        }
    }

    val gameRecordsFlow: Flow<Map<String, GameRecord>> = dao.getAllGameRecordsFlow().map { list ->
        list.associate { entity ->
            entity.gameId to GameRecord(
                gameId = entity.gameId,
                highScore = entity.highScore,
                totalStars = entity.totalStars,
                timesPlayed = entity.timesPlayed,
                bestCombo = entity.bestCombo,
                lastPlayedTimestamp = entity.lastPlayedTimestamp
            )
        }
    }

    val favoriteIdsFlow: Flow<Set<String>> = dao.getFavoriteGameIdsFlow().map { it.toSet() }

    val achievementsFlow: Flow<List<Achievement>> = dao.getAllAchievementsFlow().map { list ->
        if (list.isEmpty()) {
            ArcadeCatalog.initialAchievements
        } else {
            list.map {
                Achievement(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    iconEmoji = it.iconEmoji,
                    xpReward = it.xpReward,
                    starsReward = it.starsReward,
                    isUnlocked = it.isUnlocked,
                    progress = it.progress,
                    maxProgress = it.maxProgress
                )
            }
        }
    }

    val settingsFlow: Flow<UserSettings> = dao.getUserSettingsFlow().map { entity ->
        if (entity == null) {
            UserSettings()
        } else {
            UserSettings(
                soundEffectsEnabled = entity.soundEffectsEnabled,
                musicEnabled = entity.musicEnabled,
                masterVolume = entity.masterVolume,
                hapticsEnabled = entity.hapticsEnabled,
                defaultDifficulty = try { GameDifficulty.valueOf(entity.defaultDifficulty) } catch (_: Exception) { GameDifficulty.NORMAL },
                highContrastEnabled = entity.highContrastEnabled,
                reducedMotionEnabled = entity.reducedMotionEnabled,
                playTimeReminderMinutes = entity.playTimeReminderMinutes
            )
        }
    }

    suspend fun saveGameResult(result: GameResult): Boolean {
        val currentRecord = dao.getGameRecord(result.gameId)
        val oldHigh = currentRecord?.highScore ?: 0
        val isNewHigh = result.score > oldHigh

        val newRecord = GameRecordEntity(
            gameId = result.gameId,
            highScore = maxOf(oldHigh, result.score),
            totalStars = (currentRecord?.totalStars ?: 0) + result.starsEarned,
            timesPlayed = (currentRecord?.timesPlayed ?: 0) + 1,
            bestCombo = maxOf(currentRecord?.bestCombo ?: 0, result.combo),
            bestDurationSeconds = maxOf(currentRecord?.bestDurationSeconds ?: 0L, result.duration),
            lastPlayedTimestamp = System.currentTimeMillis()
        )
        dao.saveGameRecord(newRecord)

        val profile = dao.getPlayerProfile() ?: PlayerProfileEntity()
        val updatedProfile = profile.copy(
            xp = profile.xp + result.xpEarned,
            totalStars = profile.totalStars + result.starsEarned,
            coins = profile.coins + (result.score / 10).coerceAtLeast(1),
            gamesPlayedCount = profile.gamesPlayedCount + 1
        )
        dao.savePlayerProfile(updatedProfile)

        return isNewHigh
    }

    suspend fun toggleFavorite(gameId: String, isCurrentlyFavorite: Boolean) {
        if (isCurrentlyFavorite) {
            dao.removeFavorite(FavoriteGameEntity(gameId))
        } else {
            dao.addFavorite(FavoriteGameEntity(gameId))
        }
    }

    suspend fun updateProfile(name: String, avatarEmoji: String) {
        val profile = dao.getPlayerProfile() ?: PlayerProfileEntity()
        dao.savePlayerProfile(profile.copy(name = name, avatarEmoji = avatarEmoji))
    }

    suspend fun updateSettings(settings: UserSettings) {
        dao.saveUserSettings(
            UserSettingsEntity(
                soundEffectsEnabled = settings.soundEffectsEnabled,
                musicEnabled = settings.musicEnabled,
                masterVolume = settings.masterVolume,
                hapticsEnabled = settings.hapticsEnabled,
                defaultDifficulty = settings.defaultDifficulty.name,
                highContrastEnabled = settings.highContrastEnabled,
                reducedMotionEnabled = settings.reducedMotionEnabled,
                playTimeReminderMinutes = settings.playTimeReminderMinutes
            )
        )
    }

    suspend fun resetProgress() {
        dao.clearGameRecords()
        dao.savePlayerProfile(PlayerProfileEntity(name = "Arcade Champ", avatarEmoji = "🦊", xp = 0, totalStars = 0, coins = 0, gamesPlayedCount = 0, currentStreak = 1))
    }
}
