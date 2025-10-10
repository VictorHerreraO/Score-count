package com.soyvictorherrera.scorecount.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.soyvictorherrera.scorecount.GameStateProto
import com.soyvictorherrera.scorecount.data.mapper.toDomain
import com.soyvictorherrera.scorecount.data.mapper.toProto
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@ExperimentalCoroutinesApi
class LocalScoreDataSourceTest {

    @TempDir
    lateinit var tmpDir: File

    private lateinit var testDataStore: DataStore<GameStateProto>
    private lateinit var testScope: TestScope
    private lateinit var dataSource: LocalScoreDataSource

    @BeforeEach
    fun setUp() {
        val testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)

        testDataStore = DataStoreFactory.create(
            serializer = GameStateSerializer,
            scope = testScope,
            produceFile = { File(tmpDir, "test_game_state.pb") }
        )

        dataSource = LocalScoreDataSource(testDataStore)
    }

    @AfterEach
    fun tearDown() {
        testScope.cancel()
    }

    @Test
    fun `initial state is default GameState`() = runTest {
        // When
        val state = dataSource.gameState.first()

        // Then - should match serializer default
        val expected = GameStateSerializer.defaultValue.toDomain()
        assertEquals(expected.player1.id, state.player1.id)
        assertEquals(expected.player1.name, state.player1.name)
        assertEquals(expected.player1.score, state.player1.score)
        assertEquals(expected.player2.id, state.player2.id)
        assertEquals(expected.player2.name, state.player2.name)
        assertEquals(expected.player2.score, state.player2.score)
        assertEquals(expected.servingPlayerId, state.servingPlayerId)
        assertEquals(0, state.player1SetsWon)
        assertEquals(0, state.player2SetsWon)
        assertFalse(state.isDeuce)
        assertFalse(state.isFinished)
    }

    @Test
    fun `updateState persists new state to DataStore`() = runTest {
        // Given
        val newState = GameState(
            player1 = Player(id = 1, name = "Alice", score = 10),
            player2 = Player(id = 2, name = "Bob", score = 8),
            servingPlayerId = 1,
            player1SetsWon = 1,
            player2SetsWon = 0,
            isDeuce = false,
            isFinished = false
        )

        // When
        dataSource.updateState(newState)
        testScope.testScheduler.advanceUntilIdle()

        // Then - read directly from DataStore to verify persistence
        val persistedProto = testDataStore.data.first()
        val persistedState = persistedProto.toDomain()

        assertEquals(10, persistedState.player1.score)
        assertEquals(8, persistedState.player2.score)
        assertEquals("Alice", persistedState.player1.name)
        assertEquals("Bob", persistedState.player2.name)
        assertEquals(1, persistedState.player1SetsWon)
        assertEquals(0, persistedState.player2SetsWon)
        assertEquals(1, persistedState.servingPlayerId)
    }

    @Test
    fun `updateState updates exposed StateFlow`() = runTest {
        // Given
        val newState = GameState(
            player1 = Player(id = 1, name = "Charlie", score = 5),
            player2 = Player(id = 2, name = "Diana", score = 7),
            servingPlayerId = 2,
            player1SetsWon = 0,
            player2SetsWon = 1,
            isDeuce = false,
            isFinished = false
        )

        // When
        dataSource.updateState(newState)
        testScope.testScheduler.advanceUntilIdle()

        // Then
        val state = dataSource.gameState.first()
        assertEquals(5, state.player1.score)
        assertEquals(7, state.player2.score)
        assertEquals("Charlie", state.player1.name)
        assertEquals("Diana", state.player2.name)
        assertEquals(2, state.servingPlayerId)
    }

    @Test
    fun `updateState handles null servingPlayerId`() = runTest {
        // Given
        val newState = GameState(
            player1 = Player(id = 1, name = "Eve", score = 0),
            player2 = Player(id = 2, name = "Frank", score = 0),
            servingPlayerId = null,
            player1SetsWon = 0,
            player2SetsWon = 0,
            isDeuce = false,
            isFinished = false
        )

        // When
        dataSource.updateState(newState)
        testScope.testScheduler.advanceUntilIdle()

        // Then
        val persistedProto = testDataStore.data.first()
        val persistedState = persistedProto.toDomain()
        assertNull(persistedState.servingPlayerId)
    }

    @Test
    fun `updateState handles finished game state`() = runTest {
        // Given
        val newState = GameState(
            player1 = Player(id = 1, name = "Grace", score = 11),
            player2 = Player(id = 2, name = "Henry", score = 9),
            servingPlayerId = 1,
            player1SetsWon = 3,
            player2SetsWon = 0,
            isDeuce = false,
            isFinished = true
        )

        // When
        dataSource.updateState(newState)
        testScope.testScheduler.advanceUntilIdle()

        // Then
        val state = dataSource.gameState.first()
        assertTrue(state.isFinished)
        assertEquals(3, state.player1SetsWon)
        assertEquals(0, state.player2SetsWon)
    }

    @Test
    fun `updateState handles deuce state`() = runTest {
        // Given
        val newState = GameState(
            player1 = Player(id = 1, name = "Ivy", score = 10),
            player2 = Player(id = 2, name = "Jack", score = 10),
            servingPlayerId = 1,
            player1SetsWon = 0,
            player2SetsWon = 0,
            isDeuce = true,
            isFinished = false
        )

        // When
        dataSource.updateState(newState)
        testScope.testScheduler.advanceUntilIdle()

        // Then
        val state = dataSource.gameState.first()
        assertTrue(state.isDeuce)
        assertEquals(10, state.player1.score)
        assertEquals(10, state.player2.score)
    }

    @Test
    fun `multiple updates preserve latest state`() = runTest {
        // Given - multiple rapid updates
        val state1 = GameState(
            player1 = Player(id = 1, name = "Kate", score = 1),
            player2 = Player(id = 2, name = "Leo", score = 0),
            servingPlayerId = 1
        )
        val state2 = GameState(
            player1 = Player(id = 1, name = "Kate", score = 2),
            player2 = Player(id = 2, name = "Leo", score = 0),
            servingPlayerId = 1
        )
        val state3 = GameState(
            player1 = Player(id = 1, name = "Kate", score = 3),
            player2 = Player(id = 2, name = "Leo", score = 0),
            servingPlayerId = 1
        )

        // When
        dataSource.updateState(state1)
        dataSource.updateState(state2)
        dataSource.updateState(state3)
        testScope.testScheduler.advanceUntilIdle()

        // Then - latest state should be persisted
        val finalState = dataSource.gameState.first()
        assertEquals(3, finalState.player1.score)
    }

    @Test
    fun `persistence survives data source recreation`() = runTest {
        // Given - save state with first data source
        val savedState = GameState(
            player1 = Player(id = 1, name = "Mia", score = 15),
            player2 = Player(id = 2, name = "Noah", score = 13),
            servingPlayerId = 2,
            player1SetsWon = 2,
            player2SetsWon = 1,
            isDeuce = false,
            isFinished = false
        )

        dataSource.updateState(savedState)
        testScope.testScheduler.advanceUntilIdle()

        // When - create new data source pointing to same file
        val newDataSource = LocalScoreDataSource(testDataStore)
        testScope.testScheduler.advanceUntilIdle()

        // Then - state should be loaded from disk
        val loadedState = newDataSource.gameState.first()
        assertEquals(15, loadedState.player1.score)
        assertEquals(13, loadedState.player2.score)
        assertEquals("Mia", loadedState.player1.name)
        assertEquals("Noah", loadedState.player2.name)
        assertEquals(2, loadedState.servingPlayerId)
        assertEquals(2, loadedState.player1SetsWon)
        assertEquals(1, loadedState.player2SetsWon)
    }

    @Test
    fun `handles empty player names`() = runTest {
        // Given
        val newState = GameState(
            player1 = Player(id = 1, name = "", score = 0),
            player2 = Player(id = 2, name = "", score = 0),
            servingPlayerId = 1,
            player1SetsWon = 0,
            player2SetsWon = 0,
            isDeuce = false,
            isFinished = false
        )

        // When
        dataSource.updateState(newState)
        testScope.testScheduler.advanceUntilIdle()

        // Then
        val state = dataSource.gameState.first()
        assertEquals("", state.player1.name)
        assertEquals("", state.player2.name)
    }

    @Test
    fun `handles high score values`() = runTest {
        // Given
        val newState = GameState(
            player1 = Player(id = 1, name = "Player 1", score = 999),
            player2 = Player(id = 2, name = "Player 2", score = 888),
            servingPlayerId = 1,
            player1SetsWon = 50,
            player2SetsWon = 49,
            isDeuce = false,
            isFinished = false
        )

        // When
        dataSource.updateState(newState)
        testScope.testScheduler.advanceUntilIdle()

        // Then
        val state = dataSource.gameState.first()
        assertEquals(999, state.player1.score)
        assertEquals(888, state.player2.score)
        assertEquals(50, state.player1SetsWon)
        assertEquals(49, state.player2SetsWon)
    }
}
