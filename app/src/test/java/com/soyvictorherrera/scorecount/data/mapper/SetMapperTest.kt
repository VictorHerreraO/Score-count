package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import com.soyvictorherrera.scorecount.data.database.entity.SetEntity
import com.soyvictorherrera.scorecount.domain.model.Point
import com.soyvictorherrera.scorecount.domain.model.Set
import com.soyvictorherrera.scorecount.domain.model.SetScore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SetMapperTest {
    private lateinit var pointMapper: PointMapper
    private lateinit var mapper: SetMapper

    @BeforeEach
    fun setUp() {
        pointMapper = PointMapper()
        mapper = SetMapper(pointMapper)
    }

    @Test
    fun `mapFromEntity converts SetEntity with points to Set correctly`() {
        // Given
        val setEntity =
            SetEntity(
                id = 1L,
                matchId = 10L,
                setNumber = 1,
                finalScoreP1 = 11,
                finalScoreP2 = 9,
                winnerId = 1
            )
        val pointEntities =
            listOf(
                PointEntity(
                    id = 1L,
                    setId = 1L,
                    sequence = 1,
                    scorerId = 1,
                    player1Score = 1,
                    player2Score = 0
                ),
                PointEntity(
                    id = 2L,
                    setId = 1L,
                    sequence = 2,
                    scorerId = 2,
                    player1Score = 1,
                    player2Score = 1
                )
            )

        // When
        val result = mapper.mapFromEntity(setEntity, pointEntities)

        // Then
        assertEquals(1, result.setNumber)
        assertEquals(11, result.finalScore.player1Score)
        assertEquals(9, result.finalScore.player2Score)
        assertEquals(1, result.winnerId)
        assertEquals(2, result.points.size)
        assertEquals(1, result.points[0].sequence)
        assertEquals(2, result.points[1].sequence)
    }

    @Test
    fun `mapFromEntity handles empty points list`() {
        // Given
        val setEntity =
            SetEntity(
                id = 1L,
                matchId = 10L,
                setNumber = 2,
                finalScoreP1 = 11,
                finalScoreP2 = 5,
                winnerId = 1
            )

        // When
        val result = mapper.mapFromEntity(setEntity, emptyList())

        // Then
        assertEquals(2, result.setNumber)
        assertEquals(11, result.finalScore.player1Score)
        assertEquals(5, result.finalScore.player2Score)
        assertEquals(1, result.winnerId)
        assertEquals(0, result.points.size)
    }

    @Test
    fun `mapToEntity converts Set to SetEntity with correct matchId`() {
        // Given
        val domain =
            Set(
                setNumber = 3,
                points = emptyList(),
                finalScore =
                    SetScore(
                        player1Score = 11,
                        player2Score = 8
                    ),
                winnerId = 2
            )
        val matchId = 50L

        // When
        val result = mapper.mapToEntity(domain, matchId)

        // Then
        assertEquals(matchId, result.matchId)
        assertEquals(3, result.setNumber)
        assertEquals(11, result.finalScoreP1)
        assertEquals(8, result.finalScoreP2)
        assertEquals(2, result.winnerId)
    }

    @Test
    fun `mapPointsToEntities converts all points with correct setId`() {
        // Given
        val points =
            listOf(
                Point(
                    sequence = 1,
                    scorerId = 1,
                    player1Score = 1,
                    player2Score = 0
                ),
                Point(
                    sequence = 2,
                    scorerId = 2,
                    player1Score = 1,
                    player2Score = 1
                ),
                Point(
                    sequence = 3,
                    scorerId = 1,
                    player1Score = 2,
                    player2Score = 1
                )
            )
        val setId = 100L

        // When
        val result = mapper.mapPointsToEntities(points, setId)

        // Then
        assertEquals(3, result.size)
        assertEquals(100L, result[0].setId)
        assertEquals(100L, result[1].setId)
        assertEquals(100L, result[2].setId)
        assertEquals(1, result[0].sequence)
        assertEquals(2, result[1].sequence)
        assertEquals(3, result[2].sequence)
    }

    @Test
    fun `bidirectional mapping preserves set data`() {
        // Given
        val originalSet =
            Set(
                setNumber = 1,
                points = emptyList(),
                finalScore =
                    SetScore(
                        player1Score = 11,
                        player2Score = 9
                    ),
                winnerId = 1
            )
        val matchId = 25L

        // When
        val entity = mapper.mapToEntity(originalSet, matchId)
        val mappedBack = mapper.mapFromEntity(entity, emptyList())

        // Then
        assertEquals(originalSet.setNumber, mappedBack.setNumber)
        assertEquals(originalSet.finalScore.player1Score, mappedBack.finalScore.player1Score)
        assertEquals(originalSet.finalScore.player2Score, mappedBack.finalScore.player2Score)
        assertEquals(originalSet.winnerId, mappedBack.winnerId)
    }

    @Test
    fun `finalScore mapping works correctly`() {
        // Given
        val setEntity =
            SetEntity(
                id = 1L,
                matchId = 10L,
                setNumber = 1,
                finalScoreP1 = 15,
                finalScoreP2 = 13,
                winnerId = 1
            )

        // When
        val result = mapper.mapFromEntity(setEntity, emptyList())

        // Then
        assertEquals(15, result.finalScore.player1Score)
        assertEquals(13, result.finalScore.player2Score)
    }

    @Test
    fun `winnerId is preserved in both directions`() {
        // Given
        val domain =
            Set(
                setNumber = 2,
                points = emptyList(),
                finalScore =
                    SetScore(
                        player1Score = 9,
                        player2Score = 11
                    ),
                winnerId = 2
            )

        // When
        val entity = mapper.mapToEntity(domain, 99L)
        val mappedBack = mapper.mapFromEntity(entity, emptyList())

        // Then
        assertEquals(2, entity.winnerId)
        assertEquals(2, mappedBack.winnerId)
    }
}
