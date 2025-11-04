package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.MatchEntity
import com.soyvictorherrera.scorecount.data.database.entity.MatchWithSets
import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import com.soyvictorherrera.scorecount.data.database.entity.SetEntity
import com.soyvictorherrera.scorecount.data.database.entity.SetWithPoints
import com.soyvictorherrera.scorecount.domain.model.Match
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MatchMapperTest {
    private lateinit var pointMapper: PointMapper
    private lateinit var setMapper: SetMapper
    private lateinit var mapper: MatchMapper

    @BeforeEach
    fun setUp() {
        pointMapper = PointMapper()
        setMapper = SetMapper(pointMapper)
        mapper = MatchMapper(setMapper)
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
                date = 1609459200000L, // 2021-01-01
                winnerId = 1
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
        assertEquals(1, result.winnerId)
        assertEquals(0, result.sets.size) // Basic mapping returns empty sets
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
                date = 0L,
                winnerId = null
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
                date = 123456789L,
                winnerId = null
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
                date = 1640995200000L, // 2022-01-01
                sets = emptyList(),
                winnerId = 1
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
                date = 0L,
                sets = emptyList(),
                winnerId = null
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
                date = 999999999L,
                sets = emptyList(),
                winnerId = null
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
                date = 111111111L,
                sets = emptyList(),
                winnerId = null
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
                date = 1234567890000L,
                winnerId = 1
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
        assertEquals(originalEntity.winnerId, resultEntity.winnerId)
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
                date = 9876543210000L,
                sets = emptyList(),
                winnerId = 2
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
        assertEquals(originalMatch.winnerId, resultMatch.winnerId)
    }

    @Test
    fun `mapFromEntity includes winnerId from MatchEntity`() {
        // Given
        val entity =
            MatchEntity(
                id = 1L,
                playerOneName = "Alice",
                playerTwoName = "Bob",
                playerOneScore = 3,
                playerTwoScore = 2,
                date = 1609459200000L,
                winnerId = 1
            )

        // When
        val result = mapper.mapFromEntity(entity)

        // Then
        assertEquals(1, result.winnerId)
    }

    @Test
    @Suppress("LongMethod")
    fun `mapFromEntity with MatchWithSets includes all sets`() {
        // Given
        val matchEntity =
            MatchEntity(
                id = 1L,
                playerOneName = "Alice",
                playerTwoName = "Bob",
                playerOneScore = 3,
                playerTwoScore = 1,
                date = 1609459200000L,
                winnerId = 1
            )
        val setEntities =
            listOf(
                SetWithPoints(
                    set =
                        SetEntity(
                            id = 1L,
                            matchId = 1L,
                            setNumber = 1,
                            finalScoreP1 = 11,
                            finalScoreP2 = 9,
                            winnerId = 1
                        ),
                    points = emptyList()
                ),
                SetWithPoints(
                    set =
                        SetEntity(
                            id = 2L,
                            matchId = 1L,
                            setNumber = 2,
                            finalScoreP1 = 11,
                            finalScoreP2 = 8,
                            winnerId = 1
                        ),
                    points = emptyList()
                ),
                SetWithPoints(
                    set =
                        SetEntity(
                            id = 3L,
                            matchId = 1L,
                            setNumber = 3,
                            finalScoreP1 = 9,
                            finalScoreP2 = 11,
                            winnerId = 2
                        ),
                    points = emptyList()
                ),
                SetWithPoints(
                    set =
                        SetEntity(
                            id = 4L,
                            matchId = 1L,
                            setNumber = 4,
                            finalScoreP1 = 11,
                            finalScoreP2 = 7,
                            winnerId = 1
                        ),
                    points = emptyList()
                )
            )
        val matchWithSets = MatchWithSets(match = matchEntity, sets = setEntities)

        // When
        val result = mapper.mapFromEntity(matchWithSets)

        // Then
        assertEquals(4, result.sets.size)
        assertEquals(1, result.sets[0].setNumber)
        assertEquals(2, result.sets[1].setNumber)
        assertEquals(3, result.sets[2].setNumber)
        assertEquals(4, result.sets[3].setNumber)
    }

    @Test
    fun `mapFromEntity with MatchWithSets includes all points in each set`() {
        // Given
        val matchEntity =
            MatchEntity(
                id = 1L,
                playerOneName = "Alice",
                playerTwoName = "Bob",
                playerOneScore = 1,
                playerTwoScore = 0,
                date = 1609459200000L,
                winnerId = 1
            )
        val points =
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
                ),
                PointEntity(
                    id = 3L,
                    setId = 1L,
                    sequence = 3,
                    scorerId = 1,
                    player1Score = 2,
                    player2Score = 1
                )
            )
        val setWithPoints =
            SetWithPoints(
                set =
                    SetEntity(
                        id = 1L,
                        matchId = 1L,
                        setNumber = 1,
                        finalScoreP1 = 11,
                        finalScoreP2 = 9,
                        winnerId = 1
                    ),
                points = points
            )
        val matchWithSets = MatchWithSets(match = matchEntity, sets = listOf(setWithPoints))

        // When
        val result = mapper.mapFromEntity(matchWithSets)

        // Then
        assertEquals(1, result.sets.size)
        assertEquals(3, result.sets[0].points.size)
        assertEquals(1, result.sets[0].points[0].sequence)
        assertEquals(2, result.sets[0].points[1].sequence)
        assertEquals(3, result.sets[0].points[2].sequence)
    }

    @Test
    fun `mapFromEntity with MatchWithSets preserves set order`() {
        // Given
        val matchEntity =
            MatchEntity(
                id = 1L,
                playerOneName = "Alice",
                playerTwoName = "Bob",
                playerOneScore = 2,
                playerTwoScore = 1,
                date = 1609459200000L,
                winnerId = 1
            )
        val setEntities =
            listOf(
                SetWithPoints(
                    set =
                        SetEntity(
                            id = 3L,
                            matchId = 1L,
                            setNumber = 3,
                            finalScoreP1 = 11,
                            finalScoreP2 = 5,
                            winnerId = 1
                        ),
                    points = emptyList()
                ),
                SetWithPoints(
                    set =
                        SetEntity(
                            id = 1L,
                            matchId = 1L,
                            setNumber = 1,
                            finalScoreP1 = 11,
                            finalScoreP2 = 9,
                            winnerId = 1
                        ),
                    points = emptyList()
                ),
                SetWithPoints(
                    set =
                        SetEntity(
                            id = 2L,
                            matchId = 1L,
                            setNumber = 2,
                            finalScoreP1 = 9,
                            finalScoreP2 = 11,
                            winnerId = 2
                        ),
                    points = emptyList()
                )
            )
        val matchWithSets = MatchWithSets(match = matchEntity, sets = setEntities)

        // When
        val result = mapper.mapFromEntity(matchWithSets)

        // Then
        assertEquals(3, result.sets.size)
        // Should preserve order from MatchWithSets, not sort by setNumber
        assertEquals(3, result.sets[0].setNumber)
        assertEquals(1, result.sets[1].setNumber)
        assertEquals(2, result.sets[2].setNumber)
    }

    @Test
    fun `mapFromEntity with MatchWithSets handles empty sets list`() {
        // Given
        val matchEntity =
            MatchEntity(
                id = 1L,
                playerOneName = "Alice",
                playerTwoName = "Bob",
                playerOneScore = 0,
                playerTwoScore = 0,
                date = 1609459200000L,
                winnerId = null
            )
        val matchWithSets = MatchWithSets(match = matchEntity, sets = emptyList())

        // When
        val result = mapper.mapFromEntity(matchWithSets)

        // Then
        assertEquals(0, result.sets.size)
        assertNull(result.winnerId)
    }

    @Test
    fun `mapToEntity includes winnerId`() {
        // Given
        val match =
            Match(
                id = "99",
                playerOneName = "Charlie",
                playerTwoName = "Diana",
                playerOneScore = 5,
                playerTwoScore = 3,
                date = 1640995200000L,
                sets = emptyList(),
                winnerId = 1
            )

        // When
        val result = mapper.mapToEntity(match)

        // Then
        assertEquals(1, result.winnerId)
    }

    @Test
    fun `mapToEntity handles null winnerId`() {
        // Given
        val match =
            Match(
                id = "99",
                playerOneName = "Charlie",
                playerTwoName = "Diana",
                playerOneScore = 5,
                playerTwoScore = 3,
                date = 1640995200000L,
                sets = emptyList(),
                winnerId = null
            )

        // When
        val result = mapper.mapToEntity(match)

        // Then
        assertNull(result.winnerId)
    }
}
