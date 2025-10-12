package com.soyvictorherrera.scorecount.domain.usecase

import com.soyvictorherrera.scorecount.domain.calculator.ScoreCalculator
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for resetting the game to initial state.
 * Orchestrates: fetch current state + settings → calculate reset state → save new state.
 * Auto-determines the winner if not provided.
 */
class ResetGameUseCase
    @Inject
    constructor(
        private val scoreRepository: ScoreRepository,
        private val settingsRepository: SettingsRepository
    ) {
        suspend operator fun invoke(lastGameWinnerId: Int? = null) {
            val currentState = scoreRepository.getGameState().first()
            val settings = settingsRepository.getSettings().first()

            // Determine who should serve based on settings
            val winnerId =
                if (settings.winnerServesNextGame) {
                    // Winner serves: use provided winner or determine from state
                    lastGameWinnerId ?: ScoreCalculator.determineWinner(currentState)
                } else {
                    // Winner doesn't serve: alternate from current server
                    // If current server is player 1, next server is player 2, and vice versa
                    val currentServerId = currentState.servingPlayerId ?: currentState.player1.id
                    if (currentServerId == currentState.player1.id) {
                        currentState.player2.id
                    } else {
                        currentState.player1.id
                    }
                }

            val newState =
                ScoreCalculator.resetGame(
                    player1Id = currentState.player1.id,
                    player2Id = currentState.player2.id,
                    player1Name = currentState.player1.name,
                    player2Name = currentState.player2.name,
                    settings = settings,
                    lastGameWinnerId = winnerId
                )

            scoreRepository.updateGameState(newState)
        }
    }
