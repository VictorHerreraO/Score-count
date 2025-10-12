package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.MatchEntity
import com.soyvictorherrera.scorecount.domain.model.Match
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MatchMapperTest {
    private lateinit var mapper: MatchMapper

    @BeforeEach
    fun setUp() {
        mapper = MatchMapper()
    }

    @Test
    fun `mapFromEntity converts MatchEntity to Match correctly`() {
        // Given
        val entity =
            MatchEntity(
                id = 42L,
                playerOneName = "Alice",
                playerTwoName = "Bob",
                playerOneScore = 3,
                playerTwoScore = 2,
                date = 1609459200000L // 2021-01-01
            )

        // When
        val result = mapper.mapFromEntity(entity)

        // Then
        assertEquals("42", result.id)
        assertEquals("Alice", result.playerOneName)
        assertEquals("Bob", result.playerTwoName)
        assertEquals(3, result.playerOneScore)
        assertEquals(2, result.playerTwoScore)
        assertEquals(1609459200000L, result.date)
    }

    @Test
    fun `mapFromEntity handles entity with id 0`() {
        // Given
        val entity =
            MatchEntity(
                id = 0L,
                playerOneName = "Player1",
                playerTwoName = "Player2",
                playerOneScore = 0,
                playerTwoScore = 0,
                date = 0L
            )

        // When
        val result = mapper.mapFromEntity(entity)

        // Then
        assertEquals("0", result.id)
        assertEquals("Player1", result.playerOneName)
        assertEquals("Player2", result.playerTwoName)
        assertEquals(0, result.playerOneScore)
        assertEquals(0, result.playerTwoScore)
        assertEquals(0L, result.date)
    }

    @Test
    fun `mapFromEntity handles empty player names`() {
        // Given
        val entity =
            MatchEntity(
                id = 1L,
                playerOneName = "",
                playerTwoName = "",
                playerOneScore = 5,
                playerTwoScore = 7,
                date = 123456789L
            )

        // When
        val result = mapper.mapFromEntity(entity)

        // Then
        assertEquals("", result.playerOneName)
        assertEquals("", result.playerTwoName)
    }

    @Test
    fun `mapToEntity converts Match to MatchEntity correctly`() {
        // Given
        val match =
            Match(
                id = "99",
                playerOneName = "Charlie",
                playerTwoName = "Diana",
                playerOneScore = 5,
                playerTwoScore = 3,
                date = 1640995200000L // 2022-01-01
            )

        // When
        val result = mapper.mapToEntity(match)

        // Then
        assertEquals(99L, result.id)
        assertEquals("Charlie", result.playerOneName)
        assertEquals("Diana", result.playerTwoName)
        assertEquals(5, result.playerOneScore)
        assertEquals(3, result.playerTwoScore)
        assertEquals(1640995200000L, result.date)
    }

    @Test
    fun `mapToEntity handles Match with id "0"`() {
        // Given
        val match =
            Match(
                id = "0",
                playerOneName = "Eve",
                playerTwoName = "Frank",
                playerOneScore = 0,
                playerTwoScore = 0,
                date = 0L
            )

        // When
        val result = mapper.mapToEntity(match)

        // Then
        assertEquals(0L, result.id)
        assertEquals("Eve", result.playerOneName)
        assertEquals("Frank", result.playerTwoName)
        assertEquals(0, result.playerOneScore)
        assertEquals(0, result.playerTwoScore)
        assertEquals(0L, result.date)
    }

    @Test
    fun `mapToEntity handles Match with empty string id`() {
        // Given
        val match =
            Match(
                id = "",
                playerOneName = "Grace",
                playerTwoName = "Henry",
                playerOneScore = 1,
                playerTwoScore = 2,
                date = 999999999L
            )

        // When
        val result = mapper.mapToEntity(match)

        // Then
        assertEquals(0L, result.id) // Empty string converts to null, then defaults to 0
        assertEquals("Grace", result.playerOneName)
        assertEquals("Henry", result.playerTwoName)
    }

    @Test
    fun `mapToEntity handles Match with non-numeric id`() {
        // Given
        val match =
            Match(
                id = "not-a-number",
                playerOneName = "Ivy",
                playerTwoName = "Jack",
                playerOneScore = 4,
                playerTwoScore = 4,
                date = 111111111L
            )

        // When
        val result = mapper.mapToEntity(match)

        // Then
        assertEquals(0L, result.id) // Non-numeric string converts to null, then defaults to 0
        assertEquals("Ivy", result.playerOneName)
        assertEquals("Jack", result.playerTwoName)
    }

    @Test
    fun `bidirectional mapping from entity preserves all data`() {
        // Given
        val originalEntity =
            MatchEntity(
                id = 123L,
                playerOneName = "Kate",
                playerTwoName = "Leo",
                playerOneScore = 7,
                playerTwoScore = 5,
                date = 1234567890000L
            )

        // When - Convert entity → domain → entity
        val domain = mapper.mapFromEntity(originalEntity)
        val resultEntity = mapper.mapToEntity(domain)

        // Then - All fields should be preserved
        assertEquals(originalEntity.id, resultEntity.id)
        assertEquals(originalEntity.playerOneName, resultEntity.playerOneName)
        assertEquals(originalEntity.playerTwoName, resultEntity.playerTwoName)
        assertEquals(originalEntity.playerOneScore, resultEntity.playerOneScore)
        assertEquals(originalEntity.playerTwoScore, resultEntity.playerTwoScore)
        assertEquals(originalEntity.date, resultEntity.date)
    }

    @Test
    fun `bidirectional mapping from domain preserves all data`() {
        // Given
        val originalMatch =
            Match(
                id = "456",
                playerOneName = "Mia",
                playerTwoName = "Noah",
                playerOneScore = 9,
                playerTwoScore = 8,
                date = 9876543210000L
            )

        // When - Convert domain → entity → domain
        val entity = mapper.mapToEntity(originalMatch)
        val resultMatch = mapper.mapFromEntity(entity)

        // Then - All fields preserved
        assertEquals(originalMatch.id, resultMatch.id)
        assertEquals(originalMatch.playerOneName, resultMatch.playerOneName)
        assertEquals(originalMatch.playerTwoName, resultMatch.playerTwoName)
        assertEquals(originalMatch.playerOneScore, resultMatch.playerOneScore)
        assertEquals(originalMatch.playerTwoScore, resultMatch.playerTwoScore)
        assertEquals(originalMatch.date, resultMatch.date)
    }
}
