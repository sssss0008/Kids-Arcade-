package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.entities.*

@Database(
    entities = [
        GameRecordEntity::class,
        PlayerProfileEntity::class,
        AchievementEntity::class,
        FavoriteGameEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ArcadeDatabase : RoomDatabase() {
    abstract fun arcadeDao(): ArcadeDao

    companion object {
        @Volatile
        private var INSTANCE: ArcadeDatabase? = null

        fun getInstance(context: Context): ArcadeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArcadeDatabase::class.java,
                    "kids_arcade_mania.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
