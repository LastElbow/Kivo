package com.bustedelbow.kivo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bustedelbow.kivo.R
import com.bustedelbow.kivo.ui.theme.KivoType

/** The side of the empty state's decorative shape. */
private val DecorationSize = 96.dp

/** The glyph inside the empty state's decorative shape. */
private val DecorationGlyphSize = 40.dp

/**
 * The prompt shown when the user has no Accounts yet, guiding a fresh install to create its first
 * one (issue #3). The redesign gives it a plain rounded shape above an emphasized title and a
 * tonal call to action.
 */
@Composable
fun EmptyAccountsState(
    title: String,
    body: String,
    action: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TonalShape(
            size = DecorationSize,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_account_e_wallet),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(DecorationGlyphSize),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = KivoType.emphasized.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilledTonalButton(onClick = onAction) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = action,
                style = KivoType.emphasized.labelLarge,
            )
        }
    }
}
