package com.soyvictorherrera.scorecount.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.soyvictorherrera.scorecount.data.database.dao.MatchDao
import com.soyvictorherrera.scorecount.data.database.dao.PointDao
import com.soyvictorherrera.scorecount.data.database.dao.SetDao
import com.soyvictorherrera.scorecount.data.database.entity.MatchEntity
import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import com.soyvictorherrera.scorecount.data.database.entity.SetEntity

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add winner_id column to matches table
            database.execSQL(
                "ALTER TABLE matches ADD COLUMN winner_id INTEGER DEFAULT NULL"
            )

            // Create sets table
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS sets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    match_id INTEGER NOT NULL,
                    set_number INTEGER NOT NULL,
                    final_score_p1 INTEGER NOT NULL,
                    final_score_p2 INTEGER NOT NULL,
                    winner_id INTEGER NOT NULL,
                    FOREIGN KEY(match_id) REFERENCES matches(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )

            // Create index on sets.match_id
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_sets_match_id ON sets(match_id)"
            )

            // Create points table
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS points (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    set_id INTEGER NOT NULL,
                    sequence INTEGER NOT NULL,
                    scorer_id INTEGER NOT NULL,
                    player1_score INTEGER NOT NULL,
                    player2_score INTEGER NOT NULL,
                    FOREIGN KEY(set_id) REFERENCES sets(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )

            // Create index on points.set_id
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_points_set_id ON points(set_id)"
            )
        }
    }

@Database(
    entities = [
        MatchEntity::class,
        SetEntity::class,
        PointEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao

    abstract fun setDao(): SetDao

    abstract fun pointDao(): PointDao
}
