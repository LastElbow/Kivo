package com.bustedelbow.kivo

import android.app.Application
import com.bustedelbow.kivo.di.AppContainer

/**
 * Owns the [AppContainer] for the lifetime of the process, so the database and repository
 * are built once at startup rather than per screen.
 */
class KivoApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
