package com.bustedelbow.kivo.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.domain.model.EntrySummary
import com.bustedelbow.kivo.ui.LocalAppContainer
import com.bustedelbow.kivo.ui.components.AccountRow
import com.bustedelbow.kivo.ui.components.AmountKind
import com.bustedelbow.kivo.ui.components.AmountText
import com.bustedelbow.kivo.ui.components.AnimatedAmountText
import com.bustedelbow.kivo.ui.components.EmptyAccountsState
import com.bustedelbow.kivo.ui.components.EntryRow
import com.bustedelbow.kivo.ui.components.LoadingState
import com.bustedelbow.kivo.ui.theme.KivoType
import com.bustedelbow.kivo.ui.theme.rememberReducedMotion

/** Stable tag for Home's scrolling list, shared with UI tests. */
internal const val HOME_LIST_TEST_TAG = "home_list"

/** Home's own gutters, applied once the Scaffold's system-bar insets are already in place. */
private val ListGutters = 16.dp

/** The gap between the segmented rows of one section (M3 groups contained lists with gaps). */
private val SegmentGap = 8.dp

/** The gap that separates one section from the next; larger than [SegmentGap], and never a divider. */
private val SectionGap = 16.dp

/** Room under the last row for the extended FAB: its 56dp plus its 16dp margin, plus a breather. */
private val ListBottomPadding = 88.dp

/** The hero card's own padding; its nested containers take the radius this implies. */
private val HeroPadding = 8.dp

/** The inset the hero's Balance content and its nested week summary sit at, inside [HeroPadding]. */
private val HeroContentPadding = 16.dp

/**
 * Home: the total across active Accounts, this Week's Spend and Income, the Account list, and
 * recent history, or an empty state that guides a fresh install to create its first Account
 * (issues #3, #4; redesigned in #9).
 */
@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onCreateAccount: () -> Unit,
    onAddEntry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val viewModel: HomeViewModel =
        viewModel(factory = HomeViewModel.factory(container.accountRepository, container.entryRepository))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onCreateAccount = onCreateAccount,
        onAddEntry = onAddEntry,
        modifier = modifier,
    )
}

/**
 * The redesigned Home (issue #9): a hero `primaryContainer` Balance card that counts up, a tonal
 * week summary nested inside it, and the Accounts and Recent sections as segmented contained rows
 * separated by gaps. The extended FAB sits above the navigation bar.
 *
 * Home carries no app bar. Its own name would only restate the Home tab the navigation bar already
 * marks as selected, and the large app bar's reserved height left that stretch of screen empty; the
 * hero card is the heading instead.
 */
@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    contentPadding: PaddingValues,
    onCreateAccount: () -> Unit,
    onAddEntry: () -> Unit,
    modifier: Modifier = Modifier,
    reducedMotion: Boolean = rememberReducedMotion(),
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding),
    ) {
        when {
            uiState.isLoading -> LoadingState()

            uiState.accounts.isEmpty() ->
                EmptyAccountsState(
                    title = stringResource(R.string.home_empty_title),
                    body = stringResource(R.string.home_empty_body),
                    action = stringResource(R.string.home_empty_action),
                    onAction = onCreateAccount,
                )

            else -> {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .testTag(HOME_LIST_TEST_TAG),
                    contentPadding =
                        PaddingValues(
                            start = ListGutters,
                            top = ListGutters,
                            end = ListGutters,
                            bottom = ListBottomPadding,
                        ),
                    verticalArrangement = Arrangement.spacedBy(SegmentGap),
                ) {
                    item(key = "hero") {
                        BalanceHero(uiState = uiState, reducedMotion = reducedMotion)
                    }
                    item(key = "accounts-title") { SectionTitle(stringResource(R.string.home_accounts_title)) }
                    items(uiState.accounts, key = { "account-${it.account.id}" }) { accountBalance ->
                        TonalContainer {
                            AccountRow(
                                accountBalance = accountBalance,
                                // The segment is the container; the row paints none of its own.
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            )
                        }
                    }
                    item(key = "recent-title") { SectionTitle(stringResource(R.string.home_recent_title)) }
                    recentHistory(uiState.recentEntries)
                }

                ExtendedFloatingActionButton(
                    onClick = onAddEntry,
                    // The extended FAB draws its label, but material3 hides that label from
                    // the merged semantics, so the icon carries the accessible name.
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.home_add_entry_fab),
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.home_add_entry_fab),
                            style = KivoType.emphasized.labelLarge,
                        )
                    },
                    // The FAB is the app's most important action, so it takes the primary role.
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                )
            }
        }
    }
}

/** The recent Entries as segmented rows, or a prompt when there are none yet. */
private fun LazyListScope.recentHistory(entries: List<EntrySummary>) {
    if (entries.isEmpty()) {
        item(key = "recent-empty") {
            Text(
                text = stringResource(R.string.home_recent_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    } else {
        items(entries, key = { "entry-${it.entry.id}" }) { summary ->
            TonalContainer {
                EntryRow(
                    summary = summary,
                    // The segment is the container; the row paints none of its own.
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}

/**
 * The hero Balance container: the app's one number and its hero moment. It draws the money
 * [HomeUiState] already carries — the Balance across active Accounts and this Week's Spend and
 * Income — rather than taking those three amounts apart at the call site.
 *
 * The card takes `primaryContainer` at the shape scale's largest radius, 28dp, and the week summary
 * nested inside it is 8dp in from that edge, so it takes the nested radius the scale implies —
 * `inner = outer − padding` = 20dp — rather than the card's own.
 */
@Composable
private fun BalanceHero(
    uiState: HomeUiState,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Column(modifier = Modifier.padding(HeroPadding)) {
            Column(modifier = Modifier.padding(HeroContentPadding)) {
                Text(
                    text = stringResource(R.string.home_total_label),
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                AnimatedAmountText(
                    amountMinorUnits = uiState.totalBalanceMinorUnits,
                    kind = AmountKind.NEUTRAL,
                    style = KivoType.emphasized.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    reducedMotion = reducedMotion,
                )
            }
            WeekSummary(
                spendMinorUnits = uiState.spendMinorUnits,
                incomeMinorUnits = uiState.incomeMinorUnits,
            )
        }
    }
}

/**
 * This Week's Spend and Income as a filled tonal container nested in the hero card: Spend keeps the
 * neutral `onSurface` colour and the typographic `−`, Income takes `tertiary` and an explicit
 * `+`, so the sign carries the meaning even where the colour does not.
 */
@Composable
private fun WeekSummary(
    spendMinorUnits: Long,
    incomeMinorUnits: Long,
    modifier: Modifier = Modifier,
) {
    TonalContainer(modifier) {
        Row(modifier = Modifier.padding(HeroContentPadding)) {
            AmountColumn(
                label = stringResource(R.string.home_spend_label),
                amountMinorUnits = -spendMinorUnits,
                kind = AmountKind.EXPENSE,
                modifier = Modifier.weight(1f),
            )
            AmountColumn(
                label = stringResource(R.string.home_income_label),
                amountMinorUnits = incomeMinorUnits,
                kind = AmountKind.INCOME,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AmountColumn(
    label: String,
    amountMinorUnits: Long,
    kind: AmountKind,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        AmountText(
            amountMinorUnits = amountMinorUnits,
            kind = kind,
            style = KivoType.emphasized.titleLarge,
        )
    }
}

/** A section header: emphasized, and followed by the section's segmented rows. */
@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = KivoType.emphasized.titleMedium,
        modifier = modifier.padding(top = SectionGap, bottom = 4.dp),
    )
}

/**
 * The filled tonal container Home nests its contained groups in: the hero's week summary, and every
 * row of the Accounts and Recent sections. M3 holds contained lists apart with gaps rather than
 * dividers, so these segments are separate containers with the list's spacing between them.
 *
 * A row inside one must be given a transparent container colour: `ListItemDefaults.colors()`
 * resolves to the opaque `surface` token, which would paint over this fill and leave the row
 * looking exactly like the body behind it.
 */
@Composable
private fun TonalContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
        content = content,
    )
}
