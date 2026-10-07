package com.bustedelbow.kivo.ui.theme

import android.animation.ValueAnimator
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Kivo's hand-rolled motion spring tokens (ADR-0007).
 *
 * Stable `material3` keeps its `MotionScheme` `internal`, so the specs live here as plain
 * [SpringSpec]s any animation can reuse. The values are Material's published Expressive springs:
 *
 * - [spatialDefault] — screen and section reveals, the navigation rail expanding. Damping `0.8`.
 * - [spatialFast] — selection, chips, a button press. Damping `0.6`.
 * - [effects] — colour and opacity. Damping `1.0`, so it **never overshoots**.
 *
 * Spatial specs animate position, rotation, size and corner radius and may bounce; effects specs
 * animate colour and opacity and must not. Pick by property, then by element size.
 */
object KivoMotion {
    /** Spatial default: `0.8 / 380`. The workhorse spring. */
    fun <T> spatialDefault(visibilityThreshold: T? = null): SpringSpec<T> =
        spring(dampingRatio = 0.8f, stiffness = 380f, visibilityThreshold = visibilityThreshold)

    /** Spatial fast: `0.6 / 800`, for small elements such as a button press. */
    fun <T> spatialFast(visibilityThreshold: T? = null): SpringSpec<T> =
        spring(dampingRatio = 0.6f, stiffness = 800f, visibilityThreshold = visibilityThreshold)

    /** Effects: `1.0 / 1600`, for colour and opacity. Bounces never belong on these. */
    fun <T> effects(visibilityThreshold: T? = null): SpringSpec<T> =
        spring(dampingRatio = 1.0f, stiffness = 1600f, visibilityThreshold = visibilityThreshold)
}

/**
 * Whether the user has asked Android to reduce motion: its animator duration scale is zero, which
 * the Accessibility "Remove animations" setting also sets. Screens read this to skip a flourish and
 * snap to the resting state instead (ADR-0007's hero moments). Reads once per composition, so a
 * settings change mid-screen is not observed.
 */
@Composable
fun rememberReducedMotion(): Boolean = remember { !ValueAnimator.areAnimatorsEnabled() }
