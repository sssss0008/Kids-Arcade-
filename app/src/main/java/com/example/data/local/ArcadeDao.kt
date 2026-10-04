package com.example.data.local

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArcadeDao {

    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getPlayerProfileFlow(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getPlayerProfile(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerProfile(profile: PlayerProfileEntity)

    @Query("SELECT * FROM game_records")
    fun getAllGameRecordsFlow(): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records WHERE gameId = :gameId")
    suspend fun getGameRecord(gameId: String): GameRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameRecord(record: GameRecordEntity)

    @Query("SELECT * FROM achievements")
    fun getAllAchievementsFlow(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAchievements(achievements: List<AchievementEntity>)

    @Query("SELECT gameId FROM favorite_games")
    fun getFavoriteGameIdsFlow(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteGameEntity)

    @Delete
    suspend fun removeFavorite(fav: FavoriteGameEntity)

    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettingsFlow(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettingsEntity)

    @Query("DELETE FROM game_records")
    suspend fun clearGameRecords()
}
