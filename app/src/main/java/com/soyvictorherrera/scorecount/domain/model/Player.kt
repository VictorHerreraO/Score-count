package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents a player in the game.
 *
 * @property id Unique identifier for the player
 * @property name Player's display name
 * @property score Current score in the active set (default: 0)
 * @property color Player's color for UI visualization in hex format (default: null)
 *                Format: "#RRGGBB" (e.g., "#135BEC" for blue)
 *                Used for chart lines, UI accents, and visual identification
 */
data class Player(
    val id: Int,
    val name: String,
    val score: Int = 0,
    val color: String? = null
)
