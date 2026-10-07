package com.bustedelbow.kivo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.bustedelbow.kivo.ui.theme.KivoMotion
import com.bustedelbow.kivo.ui.theme.rememberReducedMotion
import kotlin.math.roundToLong

/**
 * An [AmountText] that counts up to its value rather than appearing at it: the redesign's hero
 * moment for the Home Balance (issue #9, ADR-0007).
 *
 * The amount springs to [amountMinorUnits] on the spatial default spring the redesign names for
 * this moment: a first load counts up from zero, and a later change counts from the value the
 * count last settled on. A change landing mid-count restarts from that settled value. When
 * [reducedMotion] is set nothing is animated and the value is already the final one.
 *
 * The count interpolates the distance between the two amounts as whole centavos rather than
 * animating an amount that is a `Float` or a `Double`: a centavo amount is a [Long], and binary
 * floating point would rest on a Balance that is not the Balance (ADR-0003). See [countUpAmount].
 */
@Composable
fun AnimatedAmountText(
    amountMinorUnits: Long,
    kind: AmountKind,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color? = null,
    reducedMotion: Boolean = rememberReducedMotion(),
) {
    AmountText(
        amountMinorUnits = rememberCountedUpAmount(amountMinorUnits, reducedMotion),
        kind = kind,
        modifier = modifier,
        style = style,
        color = color,
    )
}

/**
 * The amount to draw for a Balance of [targetMinorUnits] right now: while the count-up runs it is
 * an amount on the way there, and once it settles it is [targetMinorUnits] exactly.
 */
@Composable
private fun rememberCountedUpAmount(
    targetMinorUnits: Long,
    reducedMotion: Boolean,
): Long {
    // Where this count starts: the previous target, or zero on a first load. It is replaced with
    // the target once the count has settled, so the resting amount never depends on how far the
    // animator got.
    var countFromMinorUnits by remember { mutableLongStateOf(0L) }

    // Keyed on the target, so a changed amount starts its count in the same frame and from the
    // value the last count settled on, instead of showing the new amount and then counting to it.
    val fraction = remember(targetMinorUnits, reducedMotion) { Animatable(if (reducedMotion) 1f else 0f) }
    LaunchedEffect(targetMinorUnits, reducedMotion) {
        if (!reducedMotion) fraction.animateTo(1f, KivoMotion.spatialDefault())
        countFromMinorUnits = targetMinorUnits
    }

    return countUpAmount(countFromMinorUnits, targetMinorUnits, fraction.value)
}

/**
 * How many steps the count-up divides the distance into. Finer than the frames of the animation,
 * so the steps are never what the eye sees.
 */
private const val COUNT_UP_STEPS = 1_000L

/**
 * The amount [fraction] of the way from [fromMinorUnits] to [toMinorUnits], in whole centavos.
 *
 * Both ends are exact whatever the magnitudes: a zero fraction is [fromMinorUnits] and a fraction
 * of one is [toMinorUnits], because the fraction scales the distance in whole steps rather than
 * scaling the amount in binary floating point (ADR-0003). The spatial default spring is
 * underdamped, so a fraction above one walks past the target and back before it rests.
 */
internal fun countUpAmount(
    fromMinorUnits: Long,
    toMinorUnits: Long,
    fraction: Float,
): Long {
    val distance = toMinorUnits - fromMinorUnits
    val steps = (fraction * COUNT_UP_STEPS).roundToLong()
    return fromMinorUnits + distance * steps / COUNT_UP_STEPS
}
