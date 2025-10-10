package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.MatchEntity
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.model.Player
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
        val entity = MatchEntity(
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
        assertEquals("Alice", result.players.first.name)
        assertEquals("Bob", result.players.second.name)
        assertEquals(0, result.players.first.id) // Player IDs are always 0 in mapping
        assertEquals(0, result.players.second.id)
        assertEquals(3, result.score.first)
        assertEquals(2, result.score.second)
        assertEquals(1609459200000L, result.date)
    }

    @Test
    fun `mapFromEntity handles entity with id 0`() {
        // Given
        val entity = MatchEntity(
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
        assertEquals("Player1", result.players.first.name)
        assertEquals("Player2", result.players.second.name)
        assertEquals(0, result.score.first)
        assertEquals(0, result.score.second)
        assertEquals(0L, result.date)
    }

    @Test
    fun `mapFromEntity handles empty player names`() {
        // Given
        val entity = MatchEntity(
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
        assertEquals("", result.players.first.name)
        assertEquals("", result.players.second.name)
    }

    @Test
    fun `mapToEntity converts Match to MatchEntity correctly`() {
        // Given
        val match = Match(
            id = "99",
            players = Player(id = 1, name = "Charlie") to Player(id = 2, name = "Diana"),
            score = 5 to 3,
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
        val match = Match(
            id = "0",
            players = Player(id = 0, name = "Eve") to Player(id = 0, name = "Frank"),
            score = 0 to 0,
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
        val match = Match(
            id = "",
            players = Player(id = 1, name = "Grace") to Player(id = 2, name = "Henry"),
            score = 1 to 2,
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
        val match = Match(
            id = "not-a-number",
            players = Player(id = 1, name = "Ivy") to Player(id = 2, name = "Jack"),
            score = 4 to 4,
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
    fun `bidirectional mapping from entity preserves data`() {
        // Given
        val originalEntity = MatchEntity(
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
    fun `bidirectional mapping from domain preserves most data`() {
        // Given
        val originalMatch = Match(
            id = "456",
            players = Player(id = 10, name = "Mia") to Player(id = 20, name = "Noah"),
            score = 9 to 8,
            date = 9876543210000L
        )

        // When - Convert domain → entity → domain
        val entity = mapper.mapToEntity(originalMatch)
        val resultMatch = mapper.mapFromEntity(entity)

        // Then - Most fields preserved (except Player IDs which are always 0 in mapping)
        assertEquals(originalMatch.id, resultMatch.id)
        assertEquals(originalMatch.players.first.name, resultMatch.players.first.name)
        assertEquals(originalMatch.players.second.name, resultMatch.players.second.name)
        assertEquals(originalMatch.score.first, resultMatch.score.first)
        assertEquals(originalMatch.score.second, resultMatch.score.second)
        assertEquals(originalMatch.date, resultMatch.date)
        // Note: Player IDs are NOT preserved - they become 0 in mapFromEntity
        assertEquals(0, resultMatch.players.first.id)
        assertEquals(0, resultMatch.players.second.id)
    }
}
