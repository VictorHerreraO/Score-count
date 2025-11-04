package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import com.soyvictorherrera.scorecount.domain.model.Point
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PointMapperTest {
    private lateinit var mapper: PointMapper

    @BeforeEach
    fun setUp() {
        mapper = PointMapper()
    }

    @Test
    fun `mapFromEntity converts PointEntity to Point correctly`() {
        // Given
        val entity =
            PointEntity(
                id = 1L,
                setId = 10L,
                sequence = 1,
                scorerId = 1,
                player1Score = 1,
                player2Score = 0
            )

        // When
        val result = mapper.mapFromEntity(entity)

        // Then
        assertEquals(1, result.sequence)
        assertEquals(1, result.scorerId)
        assertEquals(1, result.player1Score)
        assertEquals(0, result.player2Score)
    }

    @Test
    fun `mapToEntity converts Point to PointEntity with correct setId`() {
        // Given
        val domain =
            Point(
                sequence = 5,
                scorerId = 2,
                player1Score = 3,
                player2Score = 4
            )
        val setId = 20L

        // When
        val result = mapper.mapToEntity(domain, setId)

        // Then
        assertEquals(setId, result.setId)
        assertEquals(5, result.sequence)
        assertEquals(2, result.scorerId)
        assertEquals(3, result.player1Score)
        assertEquals(4, result.player2Score)
    }

    @Test
    fun `mapFromEntity handles all point sequences`() {
        // Given
        val entity =
            PointEntity(
                id = 1L,
                setId = 10L,
                sequence = 15,
                scorerId = 1,
                player1Score = 8,
                player2Score = 7
            )

        // When
        val result = mapper.mapFromEntity(entity)

        // Then
        assertEquals(15, result.sequence)
        assertEquals(8, result.player1Score)
        assertEquals(7, result.player2Score)
    }

    @Test
    fun `mapToEntity preserves scorer information`() {
        // Given
        val domain =
            Point(
                sequence = 1,
                scorerId = 1,
                player1Score = 1,
                player2Score = 0
            )

        // When
        val result = mapper.mapToEntity(domain, 100L)

        // Then
        assertEquals(1, result.scorerId)
        assertEquals(1, result.player1Score)
        assertEquals(0, result.player2Score)
    }

    @Test
    fun `bidirectional mapping preserves all data`() {
        // Given
        val originalPoint =
            Point(
                sequence = 7,
                scorerId = 2,
                player1Score = 5,
                player2Score = 6
            )
        val setId = 50L

        // When
        val entity = mapper.mapToEntity(originalPoint, setId)
        val mappedBack = mapper.mapFromEntity(entity)

        // Then
        assertEquals(originalPoint.sequence, mappedBack.sequence)
        assertEquals(originalPoint.scorerId, mappedBack.scorerId)
        assertEquals(originalPoint.player1Score, mappedBack.player1Score)
        assertEquals(originalPoint.player2Score, mappedBack.player2Score)
    }

    @Test
    fun `mapToEntity correctly assigns foreign key`() {
        // Given
        val domain =
            Point(
                sequence = 1,
                scorerId = 1,
                player1Score = 0,
                player2Score = 0
            )
        val setId = 999L

        // When
        val result = mapper.mapToEntity(domain, setId)

        // Then
        assertEquals(999L, result.setId)
    }
}
