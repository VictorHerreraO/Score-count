package com.soyvictorherrera.scorecount.ui.evaluation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartEvaluationScreen(onBackClick: () -> Unit = {}) {
    val testData = TestDataGenerator

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MPAndroidChart - Selected Library") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section: Close Game (11-9)
            item {
                Text(
                    text = "Test Case 1: Close Game (11-9)",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                MpAndroidChartDemo(
                    set = testData.generateCloseGame(),
                    player1 = testData.player1,
                    player2 = testData.player2,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section: Deuce Game (15-13)
            item {
                Text(
                    text = "Test Case 2: Deuce Game (15-13)",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                MpAndroidChartDemo(
                    set = testData.generateDeuceGame(),
                    player1 = testData.player1,
                    player2 = testData.player2,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section: Landslide (11-0)
            item {
                Text(
                    text = "Test Case 3: Landslide (11-0)",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                MpAndroidChartDemo(
                    set = testData.generateLandslideGame(),
                    player1 = testData.player1,
                    player2 = testData.player2,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section: Empty Set (error handling)
            item {
                Text(
                    text = "Test Case 4: Empty Set (Error Handling)",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                MpAndroidChartDemo(
                    set = testData.generateEmptySet(),
                    player1 = testData.player1,
                    player2 = testData.player2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
