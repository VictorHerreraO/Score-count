package com.soyvictorherrera.scorecount.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointDao {
    /**
     * Insert a single point.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: PointEntity)

    /**
     * Insert multiple points in bulk.
     * Used when saving a complete set's points.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<PointEntity>)

    /**
     * Get all points for a specific set, ordered by sequence.
     */
    @Query("SELECT * FROM points WHERE set_id = :setId ORDER BY sequence ASC")
    fun getPointsForSet(setId: Long): Flow<List<PointEntity>>
}
