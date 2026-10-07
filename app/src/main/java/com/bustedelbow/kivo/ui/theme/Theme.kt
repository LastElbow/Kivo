package com.bustedelbow.kivo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

/**
 * The single theming seam (ADR-0005): Kivo's hand-authored light/dark schemes, the baseline type
 * scale paired with its emphasized set, the refined shape scale and the motion tokens.
 *
 * Dynamic colour is **off by default**; when [dynamicColor] is on and the device is Android 12+ it
 * overrides the brand palette with the user's wallpaper colours. Settings will expose the opt-in.
 */
@Composable
fun KivoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> KivoDarkColorScheme
            else -> KivoLightColorScheme
        }

    CompositionLocalProvider(LocalEmphasizedTypography provides KivoEmphasizedTypography) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = KivoShapes,
            typography = KivoTypography,
            content = content,
        )
    }
}
