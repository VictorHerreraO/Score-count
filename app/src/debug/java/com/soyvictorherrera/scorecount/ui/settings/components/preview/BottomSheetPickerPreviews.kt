package com.soyvictorherrera.scorecount.ui.settings.components.preview

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.soyvictorherrera.scorecount.ui.settings.components.BottomSheetPicker
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme

@PreviewLightDark
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun BottomSheetPickerPreview() {
    ScoreCountTheme {
        Scaffold { _ ->
            BottomSheetPicker(
                visible = true,
                onDismiss = {},
                title = "Test Title",
                options = listOf(1, 2, 3),
                selectedOption = 1,
                onOptionSelected = {},
                getOptionLabel = { "Option $it" },
                getOptionDescription = { "Description for option $it" }
            )
        }
    }
}
