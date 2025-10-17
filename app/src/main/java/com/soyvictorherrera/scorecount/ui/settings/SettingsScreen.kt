package com.soyvictorherrera.scorecount.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.soyvictorherrera.scorecount.ui.settings.components.SettingsGrid
import com.soyvictorherrera.scorecount.ui.settings.components.StepperSettingItem
import com.soyvictorherrera.scorecount.ui.settings.components.SwitchSettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsState()

    // Recompose these lists whenever settings change
    val gameControls = settingsViewModel.getGameControls(settings)
    val tableTennisRules = settingsViewModel.getTableTennisRules(settings)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                stringResource(
                                    R.string.cd_navigate_back
                                )
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
        ) {
            SectionHeader(stringResource(R.string.section_game_controls))
            SettingsGrid(items = gameControls, modifier = Modifier.padding(bottom = 16.dp))
            SectionHeader(stringResource(R.string.section_table_tennis_rules))
            SettingsList(items = tableTennisRules)

            Spacer(modifier = Modifier.height(16.dp)) // Add some padding at the bottom
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun SettingsList(items: List<SettingItemData>) {
    Column {
        items.forEach { item ->
            when (item) {
                is SettingItemData.StepperItem -> StepperSettingItem(item)
                is SettingItemData.SwitchSetting -> SwitchSettingItem(item)
                else -> {} // Should not happen in this list
            }
        }
    }
}
