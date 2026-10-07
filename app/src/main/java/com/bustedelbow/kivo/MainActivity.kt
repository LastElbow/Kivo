package com.bustedelbow.kivo

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
        setContent {
            KivoTheme {
                KivoApp()
            }
        }
    }
}
