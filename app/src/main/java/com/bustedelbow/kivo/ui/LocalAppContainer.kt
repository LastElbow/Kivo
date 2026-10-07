package com.bustedelbow.kivo.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.bustedelbow.kivo.di.AppContainer

/**
 * The process-wide [AppContainer], provided once by [KivoApp] so screens can reach the repositories
 * without each casting the application context themselves.
 */
val LocalAppContainer =
    staticCompositionLocalOf<AppContainer> {
        error("No AppContainer provided; render the UI through KivoApp")
    }
