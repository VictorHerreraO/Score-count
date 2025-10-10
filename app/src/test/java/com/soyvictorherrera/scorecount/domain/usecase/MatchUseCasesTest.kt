package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for match-related use cases:
 * - SaveMatchUseCase
 * - GetMatchesUseCase
 */
@ExperimentalCoroutinesApi
class MatchUseCasesTest {

    private lateinit var fakeMatchRepository: FakeMatchRepository

    @BeforeEach
    fun setUp() {
        fakeMatchRepository = FakeMatchRepository()
    }

    // --- SaveMatchUseCase Tests ---

    @Test
    fun `SaveMatchUseCase saves match to repository`() = runTest {
        // Given
        val useCase = SaveMatchUseCase(fakeMatchRepository)
        val match = Match(
            id = "123",
            players = Player(id = 1, name = "Alice") to Player(id = 2, name = "Bob"),
            score = 3 to 2,
            date = 1640995200000L
        )

        // When
        useCase.execute(match)

        // Then
        val savedMatch = fakeMatchRepository.lastSavedMatch
        assertEquals(match, savedMatch)
    }

    @Test
    fun `SaveMatchUseCase adds match to match list`() = runTest {
        // Given
        val useCase = SaveMatchUseCase(fakeMatchRepository)
        val match1 = Match(
            id = "1",
            players = Player(id = 1, name = "Alice") to Player(id = 2, name = "Bob"),
            score = 3 to 1,
            date = 1000L
        )
        val match2 = Match(
            id = "2",
            players = Player(id = 1, name = "Charlie") to Player(id = 2, name = "Diana"),
            score = 2 to 3,
            date = 2000L
        )

        // When
        useCase.execute(match1)
        useCase.execute(match2)

        // Then
        val matches = fakeMatchRepository.getMatchList().first()
        assertEquals(2, matches.size)
        assertTrue(matches.contains(match1))
        assertTrue(matches.contains(match2))
    }

    @Test
    fun `SaveMatchUseCase handles empty id`() = runTest {
        // Given
        val useCase = SaveMatchUseCase(fakeMatchRepository)
        val match = Match(
            id = "",
            players = Player(id = 1, name = "Eve") to Player(id = 2, name = "Frank"),
            score = 0 to 0,
            date = 3000L
        )

        // When
        useCase.execute(match)

        // Then
        val savedMatch = fakeMatchRepository.lastSavedMatch
        assertEquals("", savedMatch?.id)
        assertEquals("Eve", savedMatch?.players?.first?.name)
    }

    // --- GetMatchesUseCase Tests ---

    @Test
    fun `GetMatchesUseCase returns empty list initially`() = runTest {
        // Given
        val useCase = GetMatchesUseCase(fakeMatchRepository)

        // When
        val matches = useCase.execute().first()

        // Then
        assertTrue(matches.isEmpty())
    }

    @Test
    fun `GetMatchesUseCase returns all saved matches`() = runTest {
        // Given
        val match1 = Match(
            id = "1",
            players = Player(id = 1, name = "Alice") to Player(id = 2, name = "Bob"),
            score = 3 to 1,
            date = 1000L
        )
        val match2 = Match(
            id = "2",
            players = Player(id = 1, name = "Charlie") to Player(id = 2, name = "Diana"),
            score = 2 to 3,
            date = 2000L
        )
        val match3 = Match(
            id = "3",
            players = Player(id = 1, name = "Eve") to Player(id = 2, name = "Frank"),
            score = 5 to 4,
            date = 3000L
        )

        fakeMatchRepository.saveMatch(match1)
        fakeMatchRepository.saveMatch(match2)
        fakeMatchRepository.saveMatch(match3)

        val useCase = GetMatchesUseCase(fakeMatchRepository)

        // When
        val matches = useCase.execute().first()

        // Then
        assertEquals(3, matches.size)
        assertEquals(match1, matches[0])
        assertEquals(match2, matches[1])
        assertEquals(match3, matches[2])
    }

    @Test
    fun `GetMatchesUseCase returns Flow that updates when new matches are saved`() = runTest {
        // Given
        val useCase = GetMatchesUseCase(fakeMatchRepository)
        val matchFlow = useCase.execute()

        // Initially empty
        assertEquals(0, matchFlow.first().size)

        // When - Save a new match
        val match = Match(
            id = "1",
            players = Player(id = 1, name = "Alice") to Player(id = 2, name = "Bob"),
            score = 3 to 1,
            date = 1000L
        )
        fakeMatchRepository.saveMatch(match)

        // Then - Flow should emit updated list
        val matches = matchFlow.first()
        assertEquals(1, matches.size)
        assertEquals(match, matches[0])
    }

    // --- Fake Repository ---

    class FakeMatchRepository : MatchRepository {
        private val _matches = MutableStateFlow<List<Match>>(emptyList())
        var lastSavedMatch: Match? = null

        override fun getMatchList(): Flow<List<Match>> = _matches

        override suspend fun saveMatch(match: Match) {
            lastSavedMatch = match
            _matches.value = _matches.value + match
        }
    }
}
