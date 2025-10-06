package com.soyvictorherrera.scorecount.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.soyvictorherrera.scorecount.data.database.dao.MatchDao
import com.soyvictorherrera.scorecount.data.database.entity.MatchEntity

@Database(entities = [MatchEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
}
