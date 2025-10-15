package com.soyvictorherrera.scorecount.ui.matchhistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soyvictorherrera.scorecount.R
import com.soyvictorherrera.scorecount.domain.model.Match
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MatchHistoryScreen(
    viewModel: MatchHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val matches by viewModel.matches.collectAsState()

    Scaffold(
        topBar = {
            MatchHistoryTopBar(
                onNavigateBack = onNavigateBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(matches) { match ->
                MatchListItem(match = match)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchHistoryTopBar(onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(R.string.match_history_title)) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_back)
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            )
    )
}

@Composable
private fun MatchListItem(match: Match) {
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(match.date))

    ListItem(
        headlineContent = {
            Text(
                text =
                    stringResource(
                        id = R.string.match_list_item_headline,
                        match.playerOneName,
                        match.playerTwoName
                    ),
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Text(
                text =
                    stringResource(
                        id = R.string.match_list_item_supporting_text,
                        date,
                        match.playerOneScore,
                        match.playerTwoScore
                    )
            )
        },
        leadingContent = {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
        }
    )
}
