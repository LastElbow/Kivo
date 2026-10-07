package com.bustedelbow.kivo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bustedelbow.kivo.ui.KivoApp
import com.bustedelbow.kivo.ui.theme.KivoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Let the bottom bar's own colour reach the screen edge instead of a system scrim.
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            KivoTheme {
                KivoApp()
            }
        }
    }
}
