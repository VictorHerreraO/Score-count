package com.soyvictorherrera.scorecount.domain.model

import com.soyvictorherrera.scorecount.R

/**
 * Defines who serves at the start of a new game/set.
 */
enum class ServingRule {
    /**
     * Player 1 always serves at the start of each new game.
     */
    PLAYER_ONE_SERVES,

    /**
     * The winner of the previous game serves at the start of the next game.
     */
    WINNER_SERVES,

    /**
     * The loser of the previous game serves at the start of the next game.
     */
    LOSER_SERVES,

    /**
     * Players alternate serving between games (Player 1, then Player 2, then Player 1, etc.).
     */
    ALTERNATE;

    companion object {
        /**
         * Default serving rule.
         */
        val DEFAULT = PLAYER_ONE_SERVES
    }
}

// UI helper functions
fun ServingRule.toDisplayStringRes(): Int =
    when (this) {
        ServingRule.PLAYER_ONE_SERVES -> R.string.serving_rule_player_one_serves
        ServingRule.WINNER_SERVES -> R.string.serving_rule_winner_serves
        ServingRule.LOSER_SERVES -> R.string.serving_rule_loser_serves
        ServingRule.ALTERNATE -> R.string.serving_rule_alternate
    }

fun ServingRule.toDescriptionStringRes(): Int =
    when (this) {
        ServingRule.PLAYER_ONE_SERVES -> R.string.serving_rule_player_one_serves_description
        ServingRule.WINNER_SERVES -> R.string.serving_rule_winner_serves_description
        ServingRule.LOSER_SERVES -> R.string.serving_rule_loser_serves_description
        ServingRule.ALTERNATE -> R.string.serving_rule_alternate_description
    }
