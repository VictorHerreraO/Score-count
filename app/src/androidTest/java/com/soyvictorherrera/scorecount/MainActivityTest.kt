package com.soyvictorherrera.scorecount

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test that verifies the app launches successfully
 * and displays the ScoreScreen as the initial screen.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun appLaunchesSuccessfully() {
        // Verify that the app launches without crashing
        // The compose test rule already ensures the activity is created
        composeTestRule.waitForIdle()
    }

    @Test
    fun scoreScreenIsDisplayedOnLaunch() {
        // Verify that ScoreScreen is the initial screen by checking for player scores
        // ScoreScreen should display "0" for both players initially
        composeTestRule.waitForIdle()

        // Check that score elements are displayed (both players start at 0)
        composeTestRule.onNodeWithText("0").assertExists()
    }

    @Test
    fun scoreScreenHasNavigationMenu() {
        // Verify that the navigation menu icon is present
        composeTestRule.waitForIdle()

        // Check for the menu icon (typically "More options" or similar)
        composeTestRule.onNodeWithContentDescription(
            "More options",
            useUnmergedTree = true
        ).assertExists()
    }
}
