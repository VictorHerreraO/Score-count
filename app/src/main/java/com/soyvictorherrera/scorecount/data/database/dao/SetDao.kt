package com.soyvictorherrera.scorecount.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soyvictorherrera.scorecount.data.database.entity.SetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    /**
     * Insert a single set and return its generated ID.
     * Used when saving sets sequentially with their points.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    /**
     * Insert multiple sets in bulk and return their generated IDs.
     * Used when saving a complete match.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<SetEntity>): List<Long>

    /**
     * Get all sets for a specific match, ordered by set number.
     */
    @Query("SELECT * FROM sets WHERE match_id = :matchId ORDER BY set_number ASC")
    fun getSetsForMatch(matchId: Long): Flow<List<SetEntity>>
}
