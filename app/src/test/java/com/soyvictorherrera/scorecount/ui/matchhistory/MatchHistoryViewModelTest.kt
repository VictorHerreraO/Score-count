package com.soyvictorherrera.scorecount.ui.matchhistory

import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import com.soyvictorherrera.scorecount.domain.usecase.GetMatchesUseCase
import com.soyvictorherrera.scorecount.util.fakes.FakeMatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class MatchHistoryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MatchHistoryViewModel
    private lateinit var fakeMatchRepository: FakeMatchRepository

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeMatchRepository = FakeMatchRepository()
        val getMatchesUseCase = GetMatchesUseCase(fakeMatchRepository)
        viewModel = MatchHistoryViewModel(getMatchesUseCase, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle() // Let init block complete
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial matches list is empty`() =
        runTest {
            // When
            testDispatcher.scheduler.advanceUntilIdle()
            val matches = viewModel.matches.first()

            // Then
            assertTrue(matches.isEmpty())
        }

    @Test
    fun `matches are loaded from repository on initialization`() =
        runTest {
            // Given
            val match1 =
                Match(
                    id = "1",
                    playerOneName = "Alice",
                    playerTwoName = "Bob",
                    playerOneScore = 3,
                    playerTwoScore = 1,
                    date = 1000L
                )
            val match2 =
                Match(
                    id = "2",
                    playerOneName = "Charlie",
                    playerTwoName = "Diana",
                    playerOneScore = 2,
                    playerTwoScore = 3,
                    date = 2000L
                )

            fakeMatchRepository.saveMatch(match1)
            fakeMatchRepository.saveMatch(match2)

            // Re-create ViewModel to trigger init block with pre-populated matches
            val getMatchesUseCase = GetMatchesUseCase(fakeMatchRepository)
            viewModel = MatchHistoryViewModel(getMatchesUseCase, testDispatcher)

            // When
            testDispatcher.scheduler.advanceUntilIdle()
            val matches = viewModel.matches.first()

            // Then
            assertEquals(2, matches.size)
            assertEquals(match1, matches[0])
            assertEquals(match2, matches[1])
        }

    @Test
    fun `matches are updated when new match is added to repository`() =
        runTest {
            // Given - Initial ViewModel with no matches
            testDispatcher.scheduler.advanceUntilIdle()
            var matches = viewModel.matches.first()
            assertEquals(0, matches.size)

            // When - Add a new match
            val match =
                Match(
                    id = "1",
                    playerOneName = "Eve",
                    playerTwoName = "Frank",
                    playerOneScore = 5,
                    playerTwoScore = 3,
                    date = 3000L
                )
            fakeMatchRepository.saveMatch(match)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            matches = viewModel.matches.first()
            assertEquals(1, matches.size)
            assertEquals(match, matches[0])
        }

    @Test
    fun `matches handles errors gracefully by showing empty list`() =
        runTest {
            // Given - Repository that throws an error
            val errorRepository =
                object : MatchRepository {
                    override fun getMatchList(): Flow<List<Match>> =
                        kotlinx.coroutines.flow.flow {
                            throw IOException("Database error")
                        }

                    override suspend fun saveMatch(match: Match) {
                        // Not used in this test
                    }
                }

            val getMatchesUseCase = GetMatchesUseCase(errorRepository)
            viewModel = MatchHistoryViewModel(getMatchesUseCase, testDispatcher)

            // When
            testDispatcher.scheduler.advanceUntilIdle()
            val matches = viewModel.matches.first()

            // Then - Should show empty list instead of crashing
            assertTrue(matches.isEmpty())
        }

    @Test
    fun `matches preserves order from repository`() =
        runTest {
            // Given
            val match1 =
                Match(
                    id = "1",
                    playerOneName = "Alice",
                    playerTwoName = "Bob",
                    playerOneScore = 3,
                    playerTwoScore = 1,
                    date = 1000L
                )
            val match2 =
                Match(
                    id = "2",
                    playerOneName = "Charlie",
                    playerTwoName = "Diana",
                    playerOneScore = 2,
                    playerTwoScore = 3,
                    date = 2000L
                )
            val match3 =
                Match(
                    id = "3",
                    playerOneName = "Eve",
                    playerTwoName = "Frank",
                    playerOneScore = 5,
                    playerTwoScore = 4,
                    date = 3000L
                )

            // Save in specific order
            fakeMatchRepository.saveMatch(match1)
            fakeMatchRepository.saveMatch(match2)
            fakeMatchRepository.saveMatch(match3)

            // Re-create ViewModel
            val getMatchesUseCase = GetMatchesUseCase(fakeMatchRepository)
            viewModel = MatchHistoryViewModel(getMatchesUseCase, testDispatcher)

            // When
            testDispatcher.scheduler.advanceUntilIdle()
            val matches = viewModel.matches.first()

            // Then - Order should be preserved
            assertEquals(3, matches.size)
            assertEquals("1", matches[0].id)
            assertEquals("2", matches[1].id)
            assertEquals("3", matches[2].id)
        }
}
