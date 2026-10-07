package com.bustedelbow.kivo.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.data.preferences.AppearanceMode
import com.bustedelbow.kivo.data.preferences.AppearanceSettings
import com.bustedelbow.kivo.ui.LocalAppContainer
import com.bustedelbow.kivo.ui.theme.KivoType

/** Stable tag for the dynamic-colour row, shared with UI tests. */
internal const val SETTINGS_DYNAMIC_COLOUR_ROW_TEST_TAG = "settings_dynamic_colour_row"

/**
 * Settings: the persisted Appearance (light/dark/system plus dynamic colour) and an About section.
 * The appearance it writes is what [com.bustedelbow.kivo.ui.theme.KivoTheme] reads, so a choice
 * takes effect at once and survives the next launch.
 */
@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModel.factory(LocalAppContainer.current.appearanceRepository))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onModeSelected = viewModel::setMode,
        onDynamicColorChange = viewModel::setDynamicColor,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
    uiState: AppearanceSettings,
    contentPadding: PaddingValues,
    onModeSelected: (AppearanceMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding),
    ) {
        // The Scaffold already applied the system-bar insets, so the bar itself adds none.
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.destination_settings),
                    style = KivoType.emphasized.titleLarge,
                )
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SectionHeader(text = stringResource(R.string.settings_appearance_title))
            AppearanceModeCard(selected = uiState.mode, onSelect = onModeSelected)
            DynamicColorRow(checked = uiState.dynamicColor, onChange = onDynamicColorChange)
            SectionHeader(text = stringResource(R.string.settings_about_title))
            AboutCard()
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = KivoType.emphasized.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

/** The light/dark/system choice, as a single-choice segmented row in a contained card. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceModeCard(
    selected: AppearanceMode,
    onSelect: (AppearanceMode) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        SingleChoiceSegmentedButtonRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            AppearanceMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = mode == selected,
                    onClick = { onSelect(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = AppearanceMode.entries.size),
                    label = { Text(text = stringResource(mode.labelRes())) },
                )
            }
        }
    }
}

/** The dynamic (wallpaper) colour opt-in, as a contained row with a switch. */
@Composable
private fun DynamicColorRow(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            // The whole row toggles, so the switch is the target, not the only tap area.
            modifier =
                Modifier
                    .toggleable(value = checked, role = Role.Switch, onValueChange = onChange)
                    .testTag(SETTINGS_DYNAMIC_COLOUR_ROW_TEST_TAG),
            headlineContent = { Text(text = stringResource(R.string.settings_dynamic_colour_title)) },
            supportingContent = { Text(text = stringResource(R.string.settings_dynamic_colour_summary)) },
            trailingContent = { Switch(checked = checked, onCheckedChange = null) },
        )
    }
}

/** The About section: the app's name and, when it can be read, its version. */
@Composable
private fun AboutCard() {
    val context = LocalContext.current
    val versionName =
        remember(context) {
            runCatching {
                context.packageManager
                    .getPackageInfo(context.packageName, 0)
                    .versionName
            }.getOrNull()
        }
    val versionLabel =
        if (versionName != null) {
            stringResource(R.string.settings_about_version, versionName)
        } else {
            stringResource(R.string.settings_about_tagline)
        }

    Card(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.app_name)) },
            supportingContent = { Text(text = versionLabel) },
        )
    }
}

private fun AppearanceMode.labelRes(): Int =
    when (this) {
        AppearanceMode.LIGHT -> R.string.settings_appearance_light
        AppearanceMode.DARK -> R.string.settings_appearance_dark
        AppearanceMode.SYSTEM -> R.string.settings_appearance_system
    }
